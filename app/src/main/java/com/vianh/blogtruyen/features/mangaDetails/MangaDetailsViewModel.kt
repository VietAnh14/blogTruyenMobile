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

class MangaDetailsViewModel(
    private val repo: MangaRepo,
    private val favoriteRepo: FavoriteRepository,
    var manga: Manga
) : BaseVM() {
    val mangaLiveData: MutableLiveData<Manga> = MutableLiveData(manga)
    val chapters: MutableLiveData<List<Chapter>> = MutableLiveData(listOf())
    val comments: MutableLiveData<List<Comment>> = MutableLiveData(listOf())
    val isFavorite: LiveData<Boolean> = favoriteRepo
        .observeFavoriteState(manga.id)
        .asLiveData(viewModelScope.coroutineContext)

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
            mangaLiveData.value = repo.fetchMangaDetails(manga)
        }
    }

    private fun loadChapters() {
        launchJob {
            val fetchChapters = repo.loadChapter(manga.id)
            manga = manga.copy(chapters = fetchChapters)
            chapters.postValue(fetchChapters)
        }
    }

    fun loadComments(offset: Int = commentPage) {
        if (commentJob?.isCompleted == false || !hasNextCommentPage) {
            return
        }
        commentJob = launchJob {
            val commentMap = repo.loadComments(manga.id, offset)
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
                favoriteRepo.addToFavorite(manga)
            else
                favoriteRepo.removeFromFavorite(manga.id)
        }
    }
}