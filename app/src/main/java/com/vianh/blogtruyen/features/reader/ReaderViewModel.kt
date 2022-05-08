package com.vianh.blogtruyen.features.reader

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.repo.ProviderRepoManager
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.base.list.items.ErrorItem
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.features.base.list.items.LoadingItem
import com.vianh.blogtruyen.features.details.data.AppMangaRepo
import com.vianh.blogtruyen.features.reader.list.ReaderItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ReaderViewModel(
    state: ReaderState,
    private val providerManager: ProviderRepoManager,
    private val appMangaRepo: AppMangaRepo
) : BaseVM() {

    val isOffline = state.isOffline
    private val providerRepo = providerManager.create(isOffline)
    private var loadPageJob: Job? = null
    private val mangaFlow = MutableStateFlow(state.manga)
    private val listItems: MutableStateFlow<List<ListItem>> = MutableStateFlow(listOf(LoadingItem))
    private val currentChapter: MutableStateFlow<Chapter> = MutableStateFlow(state.chapter)
    private val manga
        get() = mangaFlow.value

    val uiState = combine(mangaFlow, currentChapter, listItems, ::ReaderModel)
        .asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)

    val currentPage = MutableStateFlow(0)
    val controllerState = combine(currentPage, listItems) { page, list ->
        val isEnable = when (list.firstOrNull()) {
            null -> false
            is LoadingItem -> false
            is ErrorItem -> false
            else -> true
        }

        var validPage = page + 1
        if (validPage > list.size) {
            validPage = 0
        }

        ControllerState(isEnable, validPage, list.size)
    }.asLiveData(Dispatchers.Default)

    val controllerVisibility = MutableLiveData(true)

    init {
        currentChapter
            .onEach { loadPages() }
            .launchIn(viewModelScope)
    }

    private suspend fun loadChaptersIfNeed() {
        if (manga.chapters.isNotEmpty()) {
            return
        }

        val sourceChapters = providerRepo.getChapters(manga.id)
        mangaFlow.value = manga.copy(chapters = sourceChapters)
    }

    fun loadPages() {
        loadPageJob?.cancel()
        loadPageJob = launchLoading {
            listItems.value = listOf(LoadingItem)
            loadChaptersIfNeed()
            val chapter = currentChapter.value
            val pages = providerRepo.getChapterPages(chapter, manga.id)

            currentChapter.value = chapter.copy(pages = pages)
            val pageItems: MutableList<ReaderItem> = pages
                .mapTo(ArrayList()) { ReaderItem.PageItem(it) }

            val transitionItemType = if (chapter.number >= manga.chapters.size) {
                ReaderItem.TransitionItem.NO_NEXT_CHAPTER
            } else {
                ReaderItem.TransitionItem.END_CURRENT
            }

            pageItems.add(ReaderItem.TransitionItem(transitionItemType, currentChapter.value))

            listItems.value = pageItems
            appMangaRepo.markChapterAsRead(chapter, manga.id)
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
        val mangaInfo = manga.withoutChapter()
        val readerState = ReaderState(mangaInfo, currentChapter.value, isOffline)
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
