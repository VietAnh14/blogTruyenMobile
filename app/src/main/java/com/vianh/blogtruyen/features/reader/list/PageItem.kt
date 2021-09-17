package com.vianh.blogtruyen.features.reader.list

import com.vianh.blogtruyen.features.base.list.items.ListItem

sealed class ReaderItem : ListItem {
    data class PageItem(val uri: String): ReaderItem() {
        override val viewType: Int = PAGE_ITEM
    }

    class TransitionItem(val transitionType: Int): ReaderItem() {

        override val viewType: Int
            get() = TRANSITION_ITEM

        companion object {
            const val END_CURRENT = 0
            const val NO_NEXT_CHAPTER = 1
        }
    }

    companion object {
        val PAGE_ITEM = ListItem.getNextViewType()
        val TRANSITION_ITEM = ListItem.getNextViewType()
    }
}