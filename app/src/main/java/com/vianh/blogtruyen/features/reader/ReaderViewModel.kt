package com.vianh.blogtruyen.features.reader

import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.list.ListItem
import com.vianh.blogtruyen.features.reader.list.ReaderItem
import timber.log.Timber

class ReaderViewModel(private val dataManager: DataManager, chapter: Chapter, val manga: Manga): BaseVM() {

    private val currentChapter: MutableLiveData<Chapter> = MutableLiveData(chapter)

    val content = MutableLiveData<ReaderModel>()

    init {
        Timber.e("Chapter size: ${manga.chapters.size}")
        loadPages()
    }

    private fun loadPages() {
        launchJob {
            val chapter = currentChapter.value ?: return@launchJob

            content.value = ReaderModel(
                manga = manga,
                chapter = chapter,
                listOf(ReaderItem.LoadingItem)
            )

            val pageItems: MutableList<ListItem> = dataManager.mangaProvider
                .fetchChapterPage(chapter.url)
                .map { ReaderItem.PageItem(it) }
                .toMutableList()

            val transitionItemType = if (getCurrentChapterPos() <= 0) {
                ReaderItem.TransitionItem.NO_NEXT_CHAPTER
            } else {
                ReaderItem.TransitionItem.END_CURRENT
            }

            pageItems.add(ReaderItem.TransitionItem(transitionItemType))
            content.value = ReaderModel(manga, chapter, pageItems)
            dataManager.dbHelper.markChapterAsRead(chapter, manga.id)
        }
    }

    // Todo: Add chapter pos in series
    fun toPreviousChapter() {
        val currentChapterPos = getCurrentChapterPos()
        if (currentChapterPos in 0 until manga.chapters.lastIndex) {
            currentChapter.value = manga.chapters[currentChapterPos + 1]
            loadPages()
        } else {
            toast.call("No previous chapter available")
        }
    }

    fun toNextChapter() {
        Timber.d("To next chapter called")
        val currentChapterPos = getCurrentChapterPos()
        if (currentChapterPos in 1..manga.chapters.lastIndex) {
            currentChapter.value = manga.chapters[currentChapterPos - 1]
            loadPages()
        } else {
            toast.call("No next chapter available")
        }
    }

    private fun getCurrentChapterPos(): Int {
        val chapterId = currentChapter.value?.id ?: return -1
        return manga.chapters.indexOfFirst { it.id == chapterId}
    }
}