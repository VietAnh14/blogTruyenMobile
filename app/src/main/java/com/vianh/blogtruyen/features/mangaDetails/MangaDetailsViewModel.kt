package com.vianh.blogtruyen.features.mangaDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.favorites.data.FavoriteRepository
import com.vianh.blogtruyen.features.mangaDetails.data.MangaRepo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class MangaDetailsViewModel(
    private val repo: MangaRepo,
    private val favoriteRepo: FavoriteRepository,
    manga: Manga
) : BaseVM() {
    private val mangaFlow: MutableStateFlow<Manga> = MutableStateFlow(manga)
    val manga = mangaFlow.asLiveData(viewModelScope.coroutineContext)

    val chapters: MutableLiveData<List<Chapter>> = MutableLiveData(listOf())
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
            mangaFlow.value = repo.fetchMangaDetails(mangaFlow.value).copy(chapters = currentManga.chapters)
        }
    }

    private fun loadChapters() {
        launchJob {
            val fetchChapters = repo.loadChapter(currentManga.id)

            mangaFlow.value = currentManga.copy(chapters = fetchChapters)
            chapters.postValue(fetchChapters)

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