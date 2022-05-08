package com.vianh.blogtruyen.features.details.ui

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
import com.vianh.blogtruyen.features.details.data.AppMangaRepo
import com.vianh.blogtruyen.features.details.ui.info.adapter.ChapterItem
import com.vianh.blogtruyen.features.details.ui.info.adapter.HeaderItem
import com.vianh.blogtruyen.features.download.DownloadService
import com.vianh.blogtruyen.features.download.DownloadState
import com.vianh.blogtruyen.features.favorites.data.FavoriteRepository
import com.vianh.blogtruyen.data.repo.LocalSourceRepo
import com.vianh.blogtruyen.data.repo.ProviderRepoManager
import com.vianh.blogtruyen.features.download.DownloadItem
import com.vianh.blogtruyen.utils.SingleLiveEvent
import com.vianh.blogtruyen.utils.ext.asLiveDataDistinct
import com.vianh.blogtruyen.utils.ext.mapToSet
import com.vianh.blogtruyen.utils.ext.mapList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MangaDetailsViewModel(
    manga: Manga,
    val isOffline: Boolean,
    private val providerRepoManager: ProviderRepoManager,
    private val appMangaRepo: AppMangaRepo,
    private val favoriteRepo: FavoriteRepository,
    private val localSourceRepo: LocalSourceRepo,
) : BaseVM() {

    private val providerRepo = providerRepoManager.create(isOffline)

    val toReaderEvent = SingleLiveEvent<Chapter>()
    val onNewPageSelected = SingleLiveEvent<Int>()

    private var commentPage = 1
    private var hasNextCommentPage = true
    private var commentJob: Job? = null
    val comments: MutableLiveData<List<Comment>> = MutableLiveData(listOf())

    private val descendingSort = MutableStateFlow(true)
    private val mangaFlow: MutableStateFlow<Manga> = MutableStateFlow(manga)
    private val localChapters = mangaFlow.map { it.id }.flatMapLatest { appMangaRepo.observeChapter(it) }
    private val remoteChapter = mangaFlow.map { it.chapters }
    val mainChapters = combine(localChapters, remoteChapter, this::combineChapters)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val chapterItems = combine(
        mainChapters,
        getDownloadState(manga),
        descendingSort,
        this::combineContent
    ).asLiveDataDistinct(Dispatchers.Default)

    val headerItem = combine(mainChapters, descendingSort) { chapters, isDescending ->
        HeaderItem(chapters.size, isDescending)
    }.asLiveDataDistinct(Dispatchers.Default)

    val favorite: LiveData<Favorite?> = favoriteRepo
        .observeFavorite(manga.id)
        .distinctUntilChangedBy { it?.manga?.id }
        .asLiveData(viewModelScope.coroutineContext)
    val isFavorite
        get() = favorite.value != null

    val readButtonState = mainChapters.map { chapters ->
        val enable = chapters.isNotEmpty()
        val chapter = chapters.firstOrNull { it.read }
        val textId = if (chapter != null) R.string.continue_reading else R.string.start_reading

        Pair(enable, textId)
    }.asLiveDataDistinct(Dispatchers.Default, Pair(false, R.string.start_reading))

    val manga = mangaFlow.asLiveData(viewModelScope.coroutineContext)
    val currentManga
        get() = mangaFlow.value

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
        val mangaDetails = providerRepo.getMangaDetails(currentManga)
        val currentChapters = currentManga.chapters
        mangaFlow.update { mangaDetails.copy(chapters = currentChapters) }
        appMangaRepo.updateMangaCategory(mangaDetails)
    }

    private suspend fun loadChapters() {
        val sourceChapters = providerRepo.getChapters(currentManga.id)
        mangaFlow.update { it.copy(chapters = sourceChapters) }

        if (!isOffline && isFavorite) {
            val newFavorite = favorite.value?.copy(
                currentChapterCount = sourceChapters.size,
                newChapterCount = 0
            ) ?: return

            favoriteRepo.upsertFavorite(newFavorite)
        }
    }

    private fun combineChapters(local: List<Chapter>, remote: List<Chapter>): List<Chapter> {
        val readIds = local.filter { it.read }.map { it.id }.toSet()
        return remote.map {
            it.read = readIds.contains(it.id)
            it
        }
    }

    private suspend fun combineContent(
        chapters: List<Chapter>,
        downloadState: Map<String, DownloadItem>,
        desc: Boolean
    ): List<ChapterItem> {
        val downloadedIds = localSourceRepo.getChapters(currentManga.id).mapToSet { it.id }
        val result = if (desc) {
            chapters.sortedByDescending { it.number }
        } else {
            chapters.sortedBy { it.number }
        }

        return result.map {
            var state: DownloadState = DownloadState.NotDownloaded
            if (downloadedIds.contains(it.id)) {
                state = DownloadState.Downloaded
            }

            val downloadItemState = downloadState[it.id]?.state ?: MutableStateFlow(state)
            ChapterItem(it, downloadItemState)
        }
    }

    private fun getDownloadState(manga: Manga) = DownloadService
        .downloadQueue
        .mapList { pair -> pair.first }
        .map { items -> items.filter { it.manga.id == manga.id } }
        .distinctUntilChanged()
        .map { downloadItem -> downloadItem.associateBy { it.chapter.id } }

    fun toggleFavorite() {
        launchJob {
            if (isFavorite) {
                favoriteRepo.removeFromFavorite(currentManga.id)
            } else {
                favoriteRepo.addToFavorite(currentManga)
            }
        }
    }

    fun toggleSortType() {
        descendingSort.update { !it }
    }

    fun continueReading() {
        val chapters = mainChapters.value
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

    fun loadComments(offset: Int = commentPage) {
        if (commentJob?.isCompleted == false || !hasNextCommentPage) {
            return
        }

        commentJob = launchJob(Dispatchers.Default) {
            val commentMap = providerRepo.getComment(currentManga.id, offset)
            hasNextCommentPage = commentMap.isNotEmpty()
            val flattenComments = ArrayList(comments.value!!)
            for (comment in commentMap) {
                flattenComments.add(comment.key)
                flattenComments.addAll(comment.value)
            }

            comments.postValue(flattenComments)
            ++commentPage
        }
    }
}