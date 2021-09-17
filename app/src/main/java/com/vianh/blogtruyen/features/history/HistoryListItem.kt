package com.vianh.blogtruyen.features.history

import com.vianh.blogtruyen.data.model.History
import com.vianh.blogtruyen.features.base.list.items.HasUniqueId
import com.vianh.blogtruyen.features.base.list.items.ListItem

sealed class HistoryListItem: ListItem {
    data class HistoryItem(val history: History, val timeString: String): HistoryListItem(),
        HasUniqueId<Int> {
        override val viewType: Int
            get() = HISTORY_ITEM

        override val id: Int
            get() = history.manga.id
    }

    data class TimeItem(val time: String): HistoryListItem() {
        override val viewType: Int
            get() = TIME_ITEM
    }

    companion object {
        val HISTORY_ITEM = ListItem.getNextViewType()
        val TIME_ITEM = ListItem.getNextViewType()
    }
}