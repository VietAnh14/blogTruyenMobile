package com.vianh.blogtruyen.features.mangaDetails

import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import kotlinx.coroutines.Job

class MangaDetailsViewModel(private val dataManager: DataManager, var manga: Manga): BaseVM() {
    val mangaLiveData: MutableLiveData<Manga> = MutableLiveData(manga)
    val chapters: MutableLiveData<List<Chapter>> = MutableLiveData(listOf())
    val comments: MutableLiveData<List<Comment>> = MutableLiveData(listOf())

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
            manga = dataManager.mangaProvider.fetchDetailManga(manga)
            dataManager.dbHelper.upsertManga(manga)
            mangaLiveData.value = manga
        }
    }

    fun loadChapters() {
        launchJob {
            val localChapterIds = dataManager
                .dbHelper
                .findAllReadChapter(manga.id)
                .map { it.id }
                .toSet()

            val mangaChapter = dataManager
                .mangaProvider
                .fetchChapterList(manga)
                .map {
                    it.read = localChapterIds.contains(it.id)
                    it
                }

            manga = manga.copy(chapters = mangaChapter)
            chapters.postValue(mangaChapter)
        }
    }

    fun loadComments(offset: Int = commentPage) {
        if (commentJob?.isCompleted == false || !hasNextCommentPage) {
            return
        }
        commentJob = launchJob {
            val commentMap = dataManager.mangaProvider.fetchComment(manga.id, offset)
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

    fun addToFavorite() {
        //TODO: Implement
    }
}