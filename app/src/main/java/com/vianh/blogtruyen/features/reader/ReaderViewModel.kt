package com.vianh.blogtruyen.features.reader

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.base.list.items.ErrorItem
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.features.base.list.items.LoadingItem
import com.vianh.blogtruyen.features.details.data.MangaRepo
import com.vianh.blogtruyen.features.local.LocalSourceRepo
import com.vianh.blogtruyen.features.reader.list.ReaderItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

class ReaderViewModel(
    state: ReaderState,
    private val mangaRepo: MangaRepo,
    private val localSourceRepo: LocalSourceRepo,
) : BaseVM() {

    val isOffline = state.isOffline
    private val manga: Manga = state.manga
    private var loadPageJob: Job? = null
    private val listItems: MutableStateFlow<List<ListItem>> = MutableStateFlow(listOf(LoadingItem))
    private val currentChapter: MutableStateFlow<Chapter> = MutableStateFlow(state.chapter)
    val currentPage = MutableStateFlow(0)

    val uiState = combine(currentChapter, listItems) { chapter, items ->
        ReaderModel(manga, chapter, items)
    }.asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)

    val controllerState = combine(currentPage, listItems) { page, list ->
        val isEnable = when (list.firstOrNull()) {
            null -> false
            is LoadingItem -> false
            is ErrorItem -> false
            else -> true
        }

        var validPage = page
        if (page > list.size) {
            validPage = 0
        }

        ControllerState(isEnable, validPage + 1, list.size)
    }.asLiveData(Dispatchers.Default)

    val controllerVisibility = MutableLiveData(true)

    init {
        currentChapter
            .map { it.id }
            .distinctUntilChanged()
            .onEach { loadPages() }
            .launchIn(viewModelScope)
    }

    fun loadPages() {
        loadPageJob?.cancel()
        loadPageJob = launchLoading {
            listItems.value = listOf(LoadingItem)
            val chapter = currentChapter.value
            val pages = if (isOffline) {
                localSourceRepo.loadPages(manga, chapter)
            } else {
                mangaRepo.getChapterPage(chapter.url)
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

            pageItems.add(ReaderItem.TransitionItem(transitionItemType, currentChapter.value))

            listItems.value = pageItems
            mangaRepo.markChapterAsRead(chapter, manga.id)
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

    fun toggleControllerVisibility() {
        controllerVisibility.value = !controllerVisibility.value!!
    }

    fun saveReaderState(bundle: Bundle) {
        val readerState = ReaderState(manga, currentChapter.value, isOffline)
        bundle.putParcelable(ReaderFragment.READER_STATE_KEY, readerState)
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
