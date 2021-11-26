package com.vianh.blogtruyen.features.base.list.items

import java.util.concurrent.atomic.AtomicInteger

interface ListItem {
    val viewType: Int

    companion object {
        private val viewTpeGenerator = AtomicInteger(0)
        fun getNextViewType() = viewTpeGenerator.incrementAndGet()

        val LOADING_ITEM = getNextViewType()
        val LOADING_FOOTER_ITEM = getNextViewType()
        val SINGLE_ITEM = getNextViewType()
        val EMPTY_ITEM = getNextViewType()
        val ERROR_ITEM = getNextViewType()
        val ERROR_FOOTER_ITEM = getNextViewType()
    }
}

interface HasUniqueId<T> {
    val id: T
}
