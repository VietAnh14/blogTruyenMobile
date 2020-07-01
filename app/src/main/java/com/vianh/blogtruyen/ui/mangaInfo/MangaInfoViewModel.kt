package com.vianh.blogtruyen.ui.mangaInfo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.Event
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MangaInfoViewModel(dataManager: DataManager) : BaseViewModel(dataManager) {
    val chapters: MutableLiveData<List<Chapter>> = MutableLiveData()
    val mangaDetail: MutableLiveData<Manga> = MutableLiveData()
    val chapterClickEvent = MutableLiveData<Event<String>>()
    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, "Exception: ${exception.stackTrace}")
    }

    fun loadData(manga: Manga) {
        uiScope.launch(exceptionHandler) {
            val chapterDefer = async {
                Log.d("Start fetch chapters", System.currentTimeMillis().toString())
                dataManager.getMangaProvider().fetchChapterList(manga)
            }
            val mangaDeferred = async {
                Log.d("Start fetch detail", System.currentTimeMillis().toString())
                dataManager.getMangaProvider().fetchDetailManga(manga)
            }
            launch {
                chapters.value = chapterDefer.await()
            }
            launch {
                mangaDetail.value = mangaDeferred.await()
            }
        }
    }

    fun onChapterClick(link: String) {
        chapterClickEvent.value = Event(link)
    }
}