package com.vianh.blogtruyen.features.base.list

class ErrorItem(val message: String): ListItem {
    override val viewType: Int
        get() = ListItem.ERROR_TYPE
}