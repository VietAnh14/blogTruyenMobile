package com.vianh.blogtruyen.features.reader

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.base.list.items.ErrorItem
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.features.base.list.items.LoadingItem
import com.vianh.blogtruyen.features.local.LocalSourceRepo
import com.vianh.blogtruyen.features.reader.list.ReaderItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
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
    val currentChapter: MutableStateFlow<Chapter> = MutableStateFlow(chapter)
    private val listItems: MutableStateFlow<List<ListItem>> =
        MutableStateFlow(listOf(LoadingItem))

    val uiState = combine(currentChapter, listItems) { chapter, items ->
        ReaderModel(manga, chapter, items)
    }.asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)

    init {
        currentChapter.map {
            loadPages()
        }.launchIn(viewModelScope)
    }

    fun loadPages() {
        loadPageJob?.cancel()
        loadPageJob = launchJob {
            listItems.value = listOf(LoadingItem)
            val chapter = currentChapter.value
            val pages = if (isOffline) {
                localSourceRepo.loadPages(manga, chapter)
            } else {
                dataManager.mangaProvider.fetchChapterPage(chapter.url)
            }
            currentChapter.value = chapter.copy(pages = pages)

            val pageItems: MutableList<ReaderItem> = pages
                .map { ReaderItem.PageItem(it) }
                .toMutableList()

            val transitionItemType = if (currentChapter.value.number >= manga.chapters.size) {
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
        val currentChapterNum = currentChapter.value.number
        val preChapter = manga.chapters.find { it.number == currentChapterNum - 1 }
        if (preChapter != null) {
            currentChapter.value = preChapter
        } else {
            toast.call("No previous chapter")
        }
    }

    fun toNextChapter() {
        val currentChapterNum = currentChapter.value.number
        val nextChapter = manga.chapters.find { it.number == currentChapterNum + 1 }
        if (nextChapter != null) {
            currentChapter.value = nextChapter
        } else {
            toast.call("No next chapter")
        }
    }

    override fun createExceptionHandler(): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            if (throwable !is CancellationException) {
                toast.call(throwable.message ?: "Unknown error")
                listItems.value = listOf(ErrorItem(throwable))
            }
        }
    }
}