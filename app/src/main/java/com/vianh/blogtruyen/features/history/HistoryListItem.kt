package com.vianh.blogtruyen.features.history

import com.vianh.blogtruyen.data.model.History
import com.vianh.blogtruyen.features.base.HasUniqueId
import com.vianh.blogtruyen.features.list.ListItem

sealed class HistoryListItem: ListItem {
    data class HistoryItem(val history: History): HistoryListItem(), HasUniqueId<Int> {
        override val viewType: Int
            get() = HISTORY_ITEM

        override val id: Int
            get() = history.manga.id
    }

    data class TimeItem(val time: String): HistoryListItem() {
        override val viewType: Int
            get() = TIME_ITEM
    }

    object EmptyItem: HistoryListItem() {
        override val viewType: Int
            get() = EMPTY_ITEM
    }

    companion object {
        const val HISTORY_ITEM = 0
        const val TIME_ITEM = 1
        const val EMPTY_ITEM = 2
    }
}