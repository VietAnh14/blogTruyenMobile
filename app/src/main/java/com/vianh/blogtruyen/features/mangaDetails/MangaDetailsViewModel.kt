package com.vianh.blogtruyen.features.mangaDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.download.DownloadService
import com.vianh.blogtruyen.features.download.DownloadState
import com.vianh.blogtruyen.features.favorites.data.FavoriteRepository
import com.vianh.blogtruyen.features.local.LocalSourceRepo
import com.vianh.blogtruyen.features.mangaDetails.data.MangaRepo
import com.vianh.blogtruyen.features.mangaDetails.mangaInfo.adapter.ChapterItem
import com.vianh.blogtruyen.utils.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

class MangaDetailsViewModel(
    private val repo: MangaRepo,
    private val favoriteRepo: FavoriteRepository,
    private val localSourceRepo: LocalSourceRepo,
    manga: Manga
) : BaseVM() {
    private val mangaFlow: MutableStateFlow<Manga> = MutableStateFlow(manga)
    val manga = mangaFlow.asLiveData(viewModelScope.coroutineContext)

    private val remoteChapters = MutableStateFlow(listOf<Chapter>())
    private val downloadingState = DownloadService
        .downloadQueue
        .mapList { pair ->
            pair.first
        }.transform {
            val mangaChapter = it.filter { it.manga.id == manga.id}
            emit(mangaChapter)
        }
        .distinctUntilChanged()
        .map { downloadItem ->
            downloadItem.associateBy { it.chapter.id }
        }

    val chapters = combine(remoteChapters, downloadingState) { remote, downloading ->
        val downloadIds = localSourceRepo.getChapters(manga.id).map { it.id }.toSet()

        remote.map {
            var state: DownloadState = DownloadState.NotDownloaded
            if (downloadIds.contains(it.id)) {
                state = DownloadState.Completed
            }

            val downloadItem = downloading[it.id]
            if (downloadItem != null) {
                ChapterItem(it, downloadItem.state.asLiveData())
            } else {
                ChapterItem(it, MutableLiveData(state))
            }
        }
    }.distinctUntilChanged().asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)


    val comments: MutableLiveData<List<Comment>> = MutableLiveData(listOf())
    val isFavorite: LiveData<Boolean> = favoriteRepo
        .observeFavoriteState(manga.id)
        .map { it != null }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext)

    val currentManga
        get() = mangaFlow.value

    private var commentPage = 1
    private var hasNextCommentPage = true
    private var commentJob: Job? = null

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
            mangaFlow.value =
                repo.fetchMangaDetails(mangaFlow.value).copy(chapters = currentManga.chapters)
        }
    }

    private fun loadChapters() {
        launchJob {
            val fetchChapters = repo.loadChapter(currentManga.id)

            mangaFlow.value = currentManga.copy(chapters = fetchChapters)
            remoteChapters.value = fetchChapters

            favoriteRepo.clearNewChapters(currentManga.id)
        }
    }

    fun loadComments(offset: Int = commentPage) {
        if (commentJob?.isCompleted == false || !hasNextCommentPage) {
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
}