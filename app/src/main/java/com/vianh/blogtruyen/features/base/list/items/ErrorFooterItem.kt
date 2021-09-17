package com.vianh.blogtruyen.features.base.list.items

data class ErrorFooterItem(val throwable: Throwable): ListItem {
    override val viewType: Int
        get() = ListItem.ERROR_FOOTER_ITEM
}