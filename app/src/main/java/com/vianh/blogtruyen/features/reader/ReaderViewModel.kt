package com.vianh.blogtruyen.features.reader

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.local.LocalSourceRepo
import com.vianh.blogtruyen.features.reader.list.ReaderItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map

class ReaderViewModel(
    private val dataManager: DataManager,
    private val localSourceRepo: LocalSourceRepo,
    chapter: Chapter,
    val manga: Manga,
    val isOffline: Boolean = false
) : BaseVM() {

    private var loadPageJob: Job? = null
    private val currentChapter: MutableStateFlow<Chapter> = MutableStateFlow(chapter)
    private val listItems: MutableStateFlow<List<ReaderItem>> =
        MutableStateFlow(listOf(ReaderItem.LoadingItem))

    val uiState = combine(currentChapter, listItems) { chapter, items ->
        ReaderModel(manga, chapter, items)
    }.asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)

    init {
        currentChapter.map {
            loadPages()
        }.launchIn(viewModelScope)
    }

    private fun loadPages() {
        loadPageJob?.cancel()
        loadPageJob = launchJob {
            listItems.value = listOf(ReaderItem.LoadingItem)
            val chapter = currentChapter.value
            val pages = if (isOffline) {
                localSourceRepo.loadPages(manga, chapter)
            } else {
                dataManager.mangaProvider.fetchChapterPage(chapter.url)
            }

            val pageItems: MutableList<ReaderItem> = pages
                .map { ReaderItem.PageItem(it) }
                .toMutableList()


            val transitionItemType = if (getCurrentChapterPos() <= 0) {
                ReaderItem.TransitionItem.NO_NEXT_CHAPTER
            } else {
                ReaderItem.TransitionItem.END_CURRENT
            }

            pageItems.add(ReaderItem.TransitionItem(transitionItemType))

            listItems.value = pageItems
            dataManager.dbHelper.markChapterAsRead(chapter, manga.id)
        }
    }

    fun toPreviousChapter() {
        val currentChapterPos = getCurrentChapterPos()
        if (currentChapterPos in 0 until manga.chapters.lastIndex) {
            currentChapter.value = manga.chapters[currentChapterPos + 1]
        } else {
            toast.call("No previous chapter available")
        }
    }

    fun toNextChapter() {
        val currentChapterPos = getCurrentChapterPos()
        if (currentChapterPos in 1..manga.chapters.lastIndex) {
            currentChapter.value = manga.chapters[currentChapterPos - 1]
        } else {
            toast.call("No next chapter available")
        }
    }

    private fun getCurrentChapterPos(): Int {
        val chapterId = currentChapter.value.id
        return manga.chapters.indexOfFirst { it.id == chapterId }
    }
}