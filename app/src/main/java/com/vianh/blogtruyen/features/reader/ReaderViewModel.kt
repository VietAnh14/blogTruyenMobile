package com.vianh.blogtruyen.features.reader

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.flow.*

class ReaderViewModel(
    state: ReaderState,
    private val dataManager: DataManager,
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

    val pageString = combine(currentPage, currentChapter) { page, chapter ->
        "${page + 1}/${chapter.pages.size}"
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

            pageItems.add(ReaderItem.TransitionItem(transitionItemType, currentChapter.value))

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

//var listImageCaption = [{"url":"https://i5.truyen-hay.com/415/415633/00-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/01.jpg","speechs":[{"coords":"508,7,512,89,651,105,785,104,786,18","text":"Ranga crimson"},{"coords":"539,214,529,305,616,361,744,349,804,295,807,213,683,137","text":"Blogtruyen vô đối"}]},{"url":"https://i5.truyen-hay.com/415/415633/02-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/03-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/04-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/05-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/06-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/07-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/08-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/09-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/10-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/11-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/12-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/13-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/14-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/15-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/16-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/17-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/18-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/19-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/20-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/21-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/22-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/23-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/24-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/25-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/26-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/27-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/28-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/29-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/30-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/31-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/32-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/33-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/34-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/35-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/36-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/37-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/38-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/39-p2j.jpg","speechs":[]},{"url":"https://i5.truyen-hay.com/415/415633/40-p2j.jpg","speechs":[]}];
//var app = angular.module('showImageApp', []);
//app.controller('showImageController', function ($scope, $http) {
//    $scope.listImageCaption = listImageCaption;
//});