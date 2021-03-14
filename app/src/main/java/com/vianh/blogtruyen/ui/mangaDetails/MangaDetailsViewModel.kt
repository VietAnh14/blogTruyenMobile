package com.vianh.blogtruyen.ui.mangaDetails

import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.ui.base.BaseViewModel
import kotlinx.coroutines.Job

class MangaDetailsViewModel(dataManager: DataManager, var manga: Manga) :
    BaseViewModel(dataManager) {
    val mangaLiveData: MutableLiveData<Manga> = MutableLiveData(manga)
    val chapters: MutableLiveData<List<Chapter>> = MutableLiveData(listOf())
    val comments: MutableLiveData<List<Comment>> = MutableLiveData(listOf())
    var commentPage = 1
    var hasNextCommentPage = true
    var commentJob: Job? = null

    init {
        loadDetails()
    }

    fun loadDetails() {
        launchLoading {
            val detailsManga = dataManager.mangaProvider.fetchDetailManga(manga)
            manga = detailsManga
            mangaLiveData.value = manga
        }
    }

    fun loadChapters() {
        launchJob {
            val mangaChapter = dataManager.mangaProvider.fetchChapterList(manga)
            manga = manga.copy(chapters = mangaChapter)
            chapters.postValue(mangaChapter)
        }
    }

    fun loadComments(offset: Int = commentPage) {
        if (commentJob?.isCompleted == false) {
            return
        }
        commentJob = launchJob {
            val commentMap = dataManager.mangaProvider.fetchComment(manga.id, offset)
            hasNextCommentPage = commentMap.isEmpty()
            val flattenComments = ArrayList(comments.value!!)
            for (comment in commentMap) {
                flattenComments.add(comment.key)
                flattenComments.addAll(comment.value)
            }
            comments.value = flattenComments
            ++commentPage
        }
    }
}