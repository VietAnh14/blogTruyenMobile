package com.vianh.blogtruyen.features.reader.list

import com.vianh.blogtruyen.features.list.ListItem

data class TransitionPageItem(
    val transitionType: Int
): ListItem {
    override val viewType: Int
        get() = ListItem.TRANSITION_ITEM

    companion object {
        const val END_CURRENT = 0
        const val NO_NEXT_CHAPTER = 1
    }
}