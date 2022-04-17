package com.vianh.blogtruyen.features.history

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.History
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.base.list.items.EmptyItem
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.features.base.list.items.LoadingItem
import com.vianh.blogtruyen.features.history.data.HistoryRepository
import com.vianh.blogtruyen.utils.SingleLiveEvent
import com.vianh.blogtruyen.utils.ifEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryViewModel(private val historyRepository: HistoryRepository) : BaseVM() {

    private val historyItems = historyRepository.observeHistory()
    private val query = MutableStateFlow("")
    private val debounceQueryFlow
        get() = query.debounce(500)
            .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val toInfoCommand = SingleLiveEvent<Manga>()

    val content = combine(
        historyItems,
        debounceQueryFlow
    ) { items, query -> filterItems(items, query) }
        .map { mapHistoryToListItem(it) }
        .ifEmpty { listOf(EmptyItem(message = "Empty history")) }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)

    private fun filterItems(histories: List<History>, query: String): List<History> {
        return if (query.isBlank())
            histories
        else
            histories.filter { it.manga.title.contains(query, true) }
    }


    private val dateFormat = "dd/MM/yyyy"
    private val timeFormat = "HH:mm"
    private fun mapHistoryToListItem(items: List<History>): List<HistoryListItem> {
        val timeMap = items.asSequence()
            .map { HistoryListItem.HistoryItem(it, timeStampToTime(it.lastRead, timeFormat)) }
            .groupBy { timeStampToTime(it.history.lastRead, dateFormat) }

        val finalList = mutableListOf<HistoryListItem>()
        for (entry in timeMap) {
            finalList.add(HistoryListItem.TimeItem(entry.key))
            finalList.addAll(entry.value)
        }

        return finalList
    }

    private fun timeStampToTime(timestamp: Long, dateFormat: String): String {
        val date = Date(timestamp)
        return SimpleDateFormat(dateFormat, Locale.ROOT).format(date)
    }

    fun clearAllHistory() {
        launchJob {
            historyRepository.clearAllHistory()
        }
    }

    fun deleteHistory(history: History) {
        launchJob {
            historyRepository.deleteHistory(history)
        }
    }

    fun filterHistory(query: String) {
        this.query.value = query
    }

    fun navigateToInfo(manga: Manga) {
        toInfoCommand.setValue(manga)
    }
}