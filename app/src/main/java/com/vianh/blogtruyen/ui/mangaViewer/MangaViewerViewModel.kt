package com.vianh.blogtruyen.ui.mangaViewer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.lang.Exception

class MangaViewerViewModel(dataManager: DataManager) : BaseViewModel(dataManager) {
    val listImage = MutableLiveData<List<String>>()

    val errorHandler = CoroutineExceptionHandler {
        _, err -> Log.e(TAG, err.stackTrace.toString())
    }

    fun getListImage(link: String) {
        uiScope.launch {
            try {
                listImage.value = dataManager.getMangaProvider().fetchChapterPage(link)
            } catch (e: Exception) {
                Log.e(TAG, e.stackTrace.toString())
            }
        }
    }
}