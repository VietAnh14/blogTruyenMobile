package com.vianh.blogtruyen.features.feed

import android.view.ViewGroup
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

    data class ErrorItem(val throwable: Throwable, val height: Int = ViewGroup.LayoutParams.MATCH_PARENT): NewFeedItem() {
        override val viewType: Int
            get() = ERROR_ITEM
    }

    data class ErrorFooter(val throwable: Throwable): NewFeedItem() {
        override val viewType: Int
            get() = ERROR_FOOTER
    }

    object LoadingItem: NewFeedItem() {
        override val viewType: Int
            get() = LOADING_ITEM
    }

    object LoadingFooter: NewFeedItem() {
        override val viewType: Int
            get() = LOADING_FOOTER

    }

    companion object {
        const val PIN_ITEM = 1
        const val UPDATE_ITEM = 2
        const val NEW_STORIES_ITEM = 3
        const val HISTORY_ITEM = 4
        const val EMPTY_ITEM = 5
        const val DETAILS_ITEM = 6
        const val LOADING_ITEM = 7
        const val LOADING_FOOTER = 8
        const val ERROR_ITEM = 9
        const val ERROR_FOOTER = 10
    }
}