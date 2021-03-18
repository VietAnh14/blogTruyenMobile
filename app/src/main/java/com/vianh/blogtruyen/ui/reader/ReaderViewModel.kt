package com.vianh.blogtruyen.ui.reader

import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.ui.base.BaseViewModel
import com.vianh.blogtruyen.ui.list.ListItem

class ReaderViewModel(dataManager: DataManager, chapter: Chapter): BaseViewModel(dataManager) {

    val pages: MutableLiveData<List<ListItem>> = MutableLiveData(listOf())
    val chapter: MutableLiveData<Chapter> = MutableLiveData(chapter)

    init {
        loadPages()
    }

    fun loadPages() {
        launchJob {
            val pageItems: MutableList<ListItem> = dataManager.mangaProvider
                .fetchChapterPage(chapter.value?.url ?: return@launchJob)
                .map { PageItem(it) }
                .toMutableList()
            pageItems.add(TransitionPageItem(TransitionPageItem.END_CURRENT))
            pages.value = pageItems
        }
    }
}