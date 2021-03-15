package com.vianh.blogtruyen.ui.reader

import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.ui.base.BaseViewModel

class ReaderViewModel(dataManager: DataManager, chapter: Chapter): BaseViewModel(dataManager) {

    val pages: MutableLiveData<List<String>> = MutableLiveData(listOf())
    val chapter: MutableLiveData<Chapter> = MutableLiveData(chapter)

    init {
        loadPages()
    }

    fun loadPages() {
        launchJob {
            pages.value = dataManager.mangaProvider.fetchChapterPage(chapter.value?.url ?: return@launchJob)
        }
    }
}