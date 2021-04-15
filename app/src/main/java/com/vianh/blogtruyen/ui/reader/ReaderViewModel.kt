package com.vianh.blogtruyen.ui.reader

import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.ui.base.BaseVM
import com.vianh.blogtruyen.ui.list.ListItem
import com.vianh.blogtruyen.ui.list.LoadingItem
import com.vianh.blogtruyen.ui.reader.list.PageItem
import com.vianh.blogtruyen.ui.reader.list.TransitionPageItem

class ReaderViewModel(private val dataManager: DataManager, chapter: Chapter, val manga: Manga): BaseVM() {

    val pages: MutableLiveData<List<ListItem>> = MutableLiveData(listOf())
    val currentChapter: MutableLiveData<Chapter> = MutableLiveData(chapter)

    var currentChapterPos = manga.chapters.indexOf(chapter)

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
            val transitionItemType = if (currentChapterPos == 0) {
                TransitionPageItem.NO_NEXT_CHAPTER
            } else {
                TransitionPageItem.END_CURRENT
            }
            pageItems.add(TransitionPageItem(transitionItemType))
            pages.value = pageItems
        }
    }

    fun hasNextChapter(): Boolean {
        val nextChapterPos = manga.chapters.indexOf(currentChapter.value) + 1
        return nextChapterPos < manga.chapters.size
    }

    fun toNextChapter() {
        currentChapterPos = (manga.chapters.indexOf(currentChapter.value) - 1).coerceAtLeast(0)
        currentChapter.value = manga.chapters[currentChapterPos]
        loadPages()
    }
}