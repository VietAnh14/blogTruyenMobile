package com.vianh.blogtruyen.features.feed.list

import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.list.items.HasUniqueId
import com.vianh.blogtruyen.features.base.list.items.ListItem

sealed class NewFeedItem: ListItem {
    data class MangaItem(val item: Manga, override val viewType: Int): NewFeedItem(),
        HasUniqueId<Int> {
        override val id: Int
            get() = item.id
    }

    companion object {
        val PIN_ITEM = ListItem.getNextViewType()
        val UPDATE_ITEM = ListItem.getNextViewType()
        val NEW_STORIES_ITEM = ListItem.getNextViewType()
        val HISTORY_ITEM = ListItem.getNextViewType()
        val DETAILS_ITEM = ListItem.getNextViewType()
        val LOADING_ITEM = ListItem.getNextViewType()
    }
}