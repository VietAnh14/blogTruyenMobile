package com.vianh.blogtruyen.features.base.list.items

import com.vianh.blogtruyen.features.base.list.items.ListItem

object LoadingItem: ListItem {
    override val viewType: Int
        get() = ListItem.LOADING_ITEM
}