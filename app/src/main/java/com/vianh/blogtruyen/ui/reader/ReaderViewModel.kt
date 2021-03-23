package com.vianh.blogtruyen.ui.reader

import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.ui.base.BaseViewModel
import com.vianh.blogtruyen.ui.list.ListItem
import com.vianh.blogtruyen.ui.list.LoadingItem

class ReaderViewModel(dataManager: DataManager, chapter: Chapter, val manga: Manga): BaseViewModel(dataManager) {

    val pages: MutableLiveData<List<ListItem>> = MutableLiveData(listOf())
    val currentChapter: MutableLiveData<Chapter> = MutableLiveData(chapter)

    init {
        loadPages()
    }

    fun loadPages() {
        launchJob {
            pages.value = listOf(LoadingItem)
            val pageItems: MutableList<ListItem> = dataManager.mangaProvider
                .fetchChapterPage(currentChapter.value?.url ?: return@launchJob)
                .map { PageItem(it) }
                .toMutableList()
            pageItems.add(TransitionPageItem(TransitionPageItem.END_CURRENT))
            pages.value = pageItems
        }
    }

    fun hasNextChapter(): Boolean {
        val nextChapterPos = manga.chapters.indexOf(currentChapter.value) + 1
        return nextChapterPos < manga.chapters.size
    }

    fun toNextChapter() {
        val currentChapterPos = (manga.chapters.indexOf(currentChapter.value) - 1).coerceAtLeast(0)
        currentChapter.value = manga.chapters[currentChapterPos]
        loadPages()
    }
}