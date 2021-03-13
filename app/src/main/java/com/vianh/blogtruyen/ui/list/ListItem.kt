package com.vianh.blogtruyen.ui.list

interface ListItem {
    val viewType: Int

    companion object {
        const val LOADING_ITEM = 1
        const val MANGA_ITEM = 2
        const val EMPTY_TYPE = 3
        const val ERROR_TYPE = 4
    }
}