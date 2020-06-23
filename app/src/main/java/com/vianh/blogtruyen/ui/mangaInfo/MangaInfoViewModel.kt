package com.vianh.blogtruyen.ui.mangaInfo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MangaInfoViewModel(dataManager: DataManager) : BaseViewModel(dataManager) {
    val chapters: MutableLiveData<List<Chapter>> = MutableLiveData()
    val mangaDetail: MutableLiveData<Manga> = MutableLiveData()

    fun loadData(manga: Manga) {
        uiScope.launch {
            try {
                val chapterDefer = async {
                    dataManager.getMangaProvider().fetchChapterList(manga)
                }
                val mangaDeferred = async {
                    dataManager.getMangaProvider().fetchDetailManga(manga)
                }
                launch {
                    chapters.value = chapterDefer.await()
                }
                launch {
                    mangaDetail.value = mangaDeferred.await()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.stackTrace.toString())
            }
        }
    }
}