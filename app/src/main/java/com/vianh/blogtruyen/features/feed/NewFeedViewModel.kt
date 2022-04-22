package com.vianh.blogtruyen.features.feed

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.base.list.items.EmptyItem
import com.vianh.blogtruyen.features.details.data.MangaRepo
import com.vianh.blogtruyen.features.feed.list.NewFeedItem
import com.vianh.blogtruyen.features.history.data.HistoryRepository
import com.vianh.blogtruyen.features.list.MangaItem
import com.vianh.blogtruyen.utils.ext.ifEmpty
import com.vianh.blogtruyen.utils.ext.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class NewFeedViewModel(private val provider: MangaRepo, historyRepository: HistoryRepository) :
    BaseVM() {

    private val pinFlow = MutableStateFlow<List<Manga>>(emptyList())
    val pinItems = pinFlow
        .mapList { MangaItem(it, NewFeedItem.PIN_ITEM) }
        .ifEmpty { listOf(EmptyItem()) }
        .asLiveData(Dispatchers.Default)

    private val updateFlow = MutableStateFlow<List<Manga>>(emptyList())
    val updateItems = updateFlow
        .mapList { MangaItem(it, NewFeedItem.UPDATE_ITEM) }
        .ifEmpty { listOf(EmptyItem()) }
        .asLiveData(Dispatchers.Default)

    private val newStoriesFlow = MutableStateFlow<List<Manga>>(emptyList())
    val newStoriesItems = newStoriesFlow
        .mapList { MangaItem(it, NewFeedItem.NEW_STORIES_ITEM) }
        .ifEmpty { listOf(EmptyItem()) }
        .asLiveData(Dispatchers.Default)

    val historyItem = historyRepository.observeHistory()
        .map { it.take(10) }
        .mapList { MangaItem(it.manga, NewFeedItem.HISTORY_ITEM) }
        .ifEmpty { listOf(EmptyItem()) }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext + Dispatchers.Main)

    init {
        loadFeed()
    }


    fun loadFeed() {
        launchLoading {
            val items = provider.getNewFeed()
            pinFlow.value = items.hotManga
            updateFlow.value = items.newUpdateManga
            newStoriesFlow.value = items.newUploadManga
        }
    }
}