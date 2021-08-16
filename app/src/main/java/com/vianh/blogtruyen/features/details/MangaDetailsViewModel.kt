package com.vianh.blogtruyen.features.details

import androidx.lifecycle.*
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.download.DownloadService
import com.vianh.blogtruyen.features.download.DownloadState
import com.vianh.blogtruyen.features.favorites.data.FavoriteRepository
import com.vianh.blogtruyen.features.local.LocalSourceRepo
import com.vianh.blogtruyen.features.details.data.MangaRepo
import com.vianh.blogtruyen.features.details.info.adapter.ChapterItem
import com.vianh.blogtruyen.features.details.info.adapter.HeaderItem
import com.vianh.blogtruyen.utils.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlin.coroutines.EmptyCoroutineContext

class MangaDetailsViewModel(
    private val repo: MangaRepo,
    private val favoriteRepo: FavoriteRepository,
    private val localSourceRepo: LocalSourceRepo,
    val isOffline: Boolean,
    manga: Manga
) : BaseVM() {

    private var commentPage = 1
    private var hasNextCommentPage = true
    private var commentJob: Job? = null
    val currentManga
        get() = mangaFlow.value

    private val mangaFlow: MutableStateFlow<Manga> = MutableStateFlow(manga)
    private val mainChapters = MutableStateFlow(listOf<Chapter>())
    private val descendingSort = MutableStateFlow(true)

    private val downloadingState = DownloadService
        .downloadQueue
        .mapList { pair -> pair.first }
        .transformLatest {
            val mangaChapter = it.filter { it.manga.id == manga.id }
            emit(mangaChapter)
        }
        .distinctUntilChanged()
        .map { downloadItem -> downloadItem.associateBy { it.chapter.id } }

    val headerItem = combine(mainChapters, descendingSort) { chapters, isDescending ->
        HeaderItem(chapters.size, isDescending)
    }.distinctUntilChanged().asLiveData(Dispatchers.Default)

    val chapters = combine(mainChapters, downloadingState, descendingSort) { main, downloading, isDescending ->
        val downloadedIds = if (isOffline)
            emptySet()
        else
            localSourceRepo.getChapters(manga.id).map { it.id }.toSet()

        val chapters = main.map {
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

        // Already sorted by des when load
        if (!isDescending) {
            chapters.sortedBy { it.chapter.number }
        } else {
            chapters
        }
    }.distinctUntilChanged().asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)

    val isFavorite: LiveData<Boolean> = favoriteRepo
        .observeFavoriteState(manga.id)
        .map { it != null }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext)

    val manga = mangaFlow.asLiveData(viewModelScope.coroutineContext)
    val comments: MutableLiveData<List<Comment>> = MutableLiveData(listOf())

    init {
        loadMangaInfo()
    }

    fun loadMangaInfo() {
        loadDetails()
        loadChapters()
    }

    private fun loadDetails() {
        launchLoading {
            // Keep current chapter
            mangaFlow.value = repo
                .fetchMangaDetails(mangaFlow.value, !isOffline)
                .copy(chapters = currentManga.chapters)
        }
    }

    private fun loadChapters() {
        launchJob {
            val fetchChapters = if (isOffline)
                localSourceRepo.getChapters(currentManga.id)
            else
                repo.loadChapter(currentManga.id)

            mangaFlow.update { it.copy(chapters = fetchChapters) }
            mainChapters.value = fetchChapters

            favoriteRepo.clearNewChapters(currentManga.id)
        }
    }

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

    fun toggleFavorite(isFavorite: Boolean) {
        launchJob {
            if (isFavorite)
                favoriteRepo.addToFavorite(currentManga)
            else
                favoriteRepo.removeFromFavorite(currentManga.id)
        }
    }

    fun toggleSortType() {
        descendingSort.update { !it }
    }
}