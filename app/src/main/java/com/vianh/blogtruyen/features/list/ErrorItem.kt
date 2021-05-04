package com.vianh.blogtruyen.features.list

class ErrorItem(val message: String): ListItem {
    override val viewType: Int
        get() = ListItem.ERROR_TYPE
}