package com.vianh.blogtruyen.features.reader.list

import com.vianh.blogtruyen.features.list.ListItem

data class PageItem(
    val uri: String
): ListItem {
    override val viewType: Int
        get() = ListItem.PAGE_ITEM
}