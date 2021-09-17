package com.vianh.blogtruyen.features.base.list.items

class ErrorItem(val throwable: Throwable? = null): ListItem {
    override val viewType: Int
        get() = ListItem.ERROR_ITEM
}