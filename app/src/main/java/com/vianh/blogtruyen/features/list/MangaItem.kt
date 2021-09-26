package com.vianh.blogtruyen.features.list

import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.list.items.HasUniqueId
import com.vianh.blogtruyen.features.base.list.items.ListItem

data class MangaItem(val manga: Manga, override val viewType: Int, val notificationCount: Int = 0): ListItem, HasUniqueId<Int> {

    override val id: Int
        get() = manga.id

    companion object {
        val MANGA_GRID_ITEM = ListItem.getNextViewType()
        val MANGA_DETAIL_LIST_ITEM = ListItem.getNextViewType()
    }
}