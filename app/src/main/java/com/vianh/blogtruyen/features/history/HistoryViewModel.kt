package com.vianh.blogtruyen.features.history

import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.features.base.BaseVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class HistoryViewModel(private val dataManager: DataManager): BaseVM() {

    val content: MutableStateFlow<List<HistoryListItem>> = MutableStateFlow(listOf())

    init {
        launchJob {
            dataManager.dbHelper
                .observeHistory()
                .onEach {
                    val items = it.map { history -> HistoryListItem.HistoryItem(history) }
                    content.value = items
                }
                .catch {
                    error.call(it)
                    content.value = listOf(HistoryListItem.EmptyItem)
                }
                .collect()
        }
    }
}