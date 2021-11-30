package com.vianh.blogtruyen.features.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.BuildConfig
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.Favorite
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.details.data.MangaRepo
import com.vianh.blogtruyen.features.details.info.adapter.ChapterItem
import com.vianh.blogtruyen.features.details.info.adapter.HeaderItem
import com.vianh.blogtruyen.features.download.DownloadService
import com.vianh.blogtruyen.features.download.DownloadState
import com.vianh.blogtruyen.features.favorites.data.FavoriteRepository
import com.vianh.blogtruyen.features.local.LocalSourceRepo
import com.vianh.blogtruyen.utils.SingleLiveEvent
import com.vianh.blogtruyen.utils.asLiveDataDistinct
import com.vianh.blogtruyen.utils.mapList
import com.vianh.blogtruyen.utils.mapToSet
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MangaDetailsViewModel(
    manga: Manga,
    val isOffline: Boolean,
    private val repo: MangaRepo,
    private val favoriteRepo: FavoriteRepository,
    private val localSourceRepo: LocalSourceRepo,
) : BaseVM() {

    // Keep scroll state
    var lastScroll = -1f

    val toReaderEvent = SingleLiveEvent<Chapter>()
    val onNewPageSelected = SingleLiveEvent<Int>()

    val currentManga
        get() = mangaFlow.value

    private val descendingSort = MutableStateFlow(true)
    private val mangaFlow: MutableStateFlow<Manga> = MutableStateFlow(manga)
    private val localChapters = mangaFlow.map { it.id }.flatMapLatest { repo.observeChapter(it) }
    private val remoteChapter = MutableStateFlow<List<Chapter>>(emptyList())
    private val mainChapters = combine(localChapters, remoteChapter) { local, remote ->
        val readIds = local.filter { it.read }.map { it.id }.toSet()
        remote.map {
            it.read = readIds.contains(it.id)
            it
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val downloadingState = DownloadService
        .downloadQueue
        .mapList { pair -> pair.first }
        .map { items -> items.filter { it.manga.id == manga.id } }
        .distinctUntilChanged()
        .map { downloadItem -> downloadItem.associateBy { it.chapter.id } }

    val chapters = combine(
        mainChapters,
        descendingSort
    ) { newChapters, descending ->
        // Already sorted by des when load
        if (descending) {
            newChapters
        } else {
            newChapters.sortedBy { it.number }
        }
    }.stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, listOf())

    val chapterItems = combine(
        chapters,
        downloadingState
    ) { mainChapters, downloading ->
        val downloadedIds = if (isOffline)
            emptySet()
        else
            localSourceRepo.getChapters(manga.id).mapToSet { it.id }

        mainChapters.map {
            var state: DownloadState = DownloadState.NotDownloaded
            if (isOffline || downloadedIds.contains(it.id)) {
                state = DownloadState.Completed
            }

            val downloadItem = downloading[it.id]
            if (downloadItem != null) {
                ChapterItem(it, downloadItem.state)
            } else {
                ChapterItem(it, MutableStateFlow(state))
            }
        }
    }.asLiveDataDistinct(Dispatchers.Default)

    val headerItem = combine(mainChapters, descendingSort) { chapters, isDescending ->
        HeaderItem(chapters.size, isDescending)
    }.asLiveDataDistinct(Dispatchers.Default)

    val favorite: LiveData<Favorite?> = favoriteRepo
        .observeFavorite(manga.id)
        .distinctUntilChangedBy { it?.manga?.id }
        .asLiveData(viewModelScope.coroutineContext)

    val readButtonState = mainChapters.map { chapters ->
        val enable = chapters.isNotEmpty()
        val chapter = chapters.firstOrNull { it.read }
        val textId = if (chapter != null) R.string.continue_reading else R.string.start_reading

        Pair(enable, textId)
    }.asLiveDataDistinct(Dispatchers.Default, Pair(false, R.string.start_reading))

    val manga = mangaFlow.asLiveData(viewModelScope.coroutineContext)

    init {
        loadMangaInfo()
    }

    fun loadMangaInfo() {
        launchLoading {
            supervisorScope {
                launch { loadDetails() }
                launch { loadChapters() }
            }
        }
    }

    private suspend fun loadDetails() {
        // Keep current chapter
        mangaFlow.value = repo
            .fetchMangaDetails(currentManga, !isOffline)
            .copy(chapters = currentManga.chapters)
    }

    private suspend fun loadChapters() {
        val fetchChapters = if (isOffline)
            localSourceRepo.getChapters(currentManga.id)
        else
            repo.loadChapters(currentManga.id)

        mangaFlow.update { it.copy(chapters = fetchChapters) }
        remoteChapter.value = fetchChapters

        if (!isOffline && isFavorite()) {
            val newFavorite = favorite.value?.copy(
                currentChapterCount = fetchChapters.size,
                newChapterCount = 0
            ) ?: return

            favoriteRepo.upsertFavorite(newFavorite)
        }
    }

    fun toggleFavorite() {
        launchJob {
            if (isFavorite())
                favoriteRepo.removeFromFavorite(currentManga.id)
            else
                favoriteRepo.addToFavorite(currentManga)
        }
    }

    private fun isFavorite() = favorite.value != null

    fun toggleSortType() {
        descendingSort.update { !it }
    }

    fun continueReading() {
        val chapters = remoteChapter.value
        val lastReadChapter = chapters
            .filter { it.read }
            .maxByOrNull { it.number } ?: chapters.first { it.number == 1 }

        toReaderEvent.setValue(lastReadChapter)
    }

    fun selectPage(pos: Int) {
        onNewPageSelected.setValue(pos)
    }

    fun getMangaUrl(): String {
        return BuildConfig.HOST + manga.value?.link.orEmpty()
    }


    // Start comments
    val comments: MutableLiveData<List<Comment>> = MutableLiveData(listOf())

    private var commentPage = 1
    private var hasNextCommentPage = true
    private var commentJob: Job? = null

    fun loadComments(offset: Int = commentPage) {
        if (commentJob?.isCompleted == false || !hasNextCommentPage || isOffline) {
            return
        }

        commentJob = launchJob {
            val commentMap = repo.loadComments(mangaFlow.value.id, offset)
            hasNextCommentPage = commentMap.isNotEmpty()
            val flattenComments = ArrayList(comments.value!!)
            for (comment in commentMap) {
                flattenComments.add(comment.key)
                flattenComments.addAll(comment.value)
            }

            comments.value = flattenComments
            ++commentPage
        }
    }

    // End comment
}