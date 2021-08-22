package com.vianh.blogtruyen.features.feed

import androidx.annotation.DrawableRes
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.list.HasUniqueId
import com.vianh.blogtruyen.features.base.list.ListItem

sealed class NewFeedItem: ListItem {
    data class MangaItem(val item: Manga, override val viewType: Int): NewFeedItem(),
        HasUniqueId<Int> {
        override val id: Int
            get() = item.id
    }


    data class EmptyItem(@DrawableRes val icon: Int = R.drawable.kurama, val message: String = "No stories found"): NewFeedItem() {
        override val viewType: Int
            get() = EMPTY_ITEM
    }

    companion object {
        const val PIN_ITEM = 1
        const val UPDATE_ITEM = 2
        const val NEW_STORIES_ITEM = 3
        const val HISTORY_ITEM = 4
        const val EMPTY_ITEM = 5
    }
}