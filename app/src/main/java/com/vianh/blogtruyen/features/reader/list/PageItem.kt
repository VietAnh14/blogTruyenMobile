package com.vianh.blogtruyen.features.reader.list

import com.vianh.blogtruyen.features.base.list.ListItem

sealed class ReaderItem : ListItem {
    data class PageItem(val uri: String): ReaderItem() {
        override val viewType: Int = ListItem.PAGE_ITEM
    }

    data class ErrorItem(val exception: Throwable? = null): ReaderItem() {
        override val viewType: Int
            get() = ListItem.ERROR_TYPE
    }

    object LoadingItem: ReaderItem() {
        override val viewType: Int
            get() = ListItem.LOADING_ITEM
    }

    class TransitionItem(val transitionType: Int): ReaderItem() {

        override val viewType: Int
            get() = ListItem.TRANSITION_ITEM

        companion object {
            const val END_CURRENT = 0
            const val NO_NEXT_CHAPTER = 1
        }
    }
}