package com.vianh.blogtruyen.ui.reader

import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.ui.list.ListItem

data class PageItem(
    val uri: String
): ListItem {
    override val viewType: Int
        get() = ListItem.PAGE_ITEM
}