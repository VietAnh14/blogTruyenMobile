package com.vianh.blogtruyen.features.list

interface ListItem {
    val viewType: Int

    companion object {
        const val LOADING_ITEM = 1
        const val SINGLE_ITEM_TYPE = 2
        const val EMPTY_TYPE = 3
        const val ERROR_TYPE = 4
        const val CHAPTER_ITEM = 5
        const val PAGE_ITEM = 6
        const val TRANSITION_ITEM = 7
    }
}

interface HasUniqueId<T> {
    val id: T
}
