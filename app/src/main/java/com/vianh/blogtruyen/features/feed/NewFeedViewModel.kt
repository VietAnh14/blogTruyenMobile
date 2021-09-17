package com.vianh.blogtruyen.features.feed

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.data.remote.MangaProvider
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.base.list.items.EmptyItem
import com.vianh.blogtruyen.features.feed.list.NewFeedItem
import com.vianh.blogtruyen.features.history.data.HistoryRepository
import com.vianh.blogtruyen.utils.ifEmpty
import com.vianh.blogtruyen.utils.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class NewFeedViewModel(private val provider: MangaProvider, historyRepository: HistoryRepository) :
    BaseVM() {

    private val pinFlow = MutableStateFlow<List<Manga>>(emptyList())
    val pinItems = pinFlow
        .mapList { NewFeedItem.MangaItem(it, NewFeedItem.PIN_ITEM) }
        .ifEmpty { listOf(EmptyItem()) }
        .asLiveData(Dispatchers.Default)

    private val updateFlow = MutableStateFlow<List<Manga>>(emptyList())
    val updateItems = updateFlow
        .mapList { NewFeedItem.MangaItem(it, NewFeedItem.UPDATE_ITEM) }
        .ifEmpty { listOf(EmptyItem()) }
        .asLiveData(Dispatchers.Default)

    private val newStoriesFlow = MutableStateFlow<List<Manga>>(emptyList())
    val newStoriesItems = newStoriesFlow
        .mapList { NewFeedItem.MangaItem(it, NewFeedItem.NEW_STORIES_ITEM) }
        .ifEmpty { listOf(EmptyItem()) }
        .asLiveData(Dispatchers.Default)

    val historyItem = historyRepository.observeHistory()
        .map { it.take(10) }
        .mapList { NewFeedItem.MangaItem(it.manga, NewFeedItem.HISTORY_ITEM) }
        .ifEmpty { listOf(EmptyItem()) }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext + Dispatchers.Main)

    init {
        loadFeed()
    }


    fun loadFeed() {
        launchLoading {
            val items = provider.fetchNewFeed()
            pinFlow.value = items.pinStories
            updateFlow.value = items.newUpdate
            newStoriesFlow.value = items.newStories
        }
    }
}