package com.vianh.blogtruyen.utils

import com.vianh.blogtruyen.features.base.list.items.*

class ItemListHelper<T: ListItem> {
    fun combine(newItems: List<T>, hasNext: Boolean, error: Throwable?): List<ListItem> {
        if (error != null) {
            val isFirstPage = newItems.getOrNull(0)?.viewType == ListItem.LOADING_ITEM
            if (isFirstPage) {
                return listOf(ErrorItem(error))
            } else
                return newItems + ErrorFooterItem(error)
        }

        if (newItems.isEmpty())
            return listOf(EmptyItem())

        if (hasNext)
            return newItems + LoadingFooterItem

        return newItems
    }
}