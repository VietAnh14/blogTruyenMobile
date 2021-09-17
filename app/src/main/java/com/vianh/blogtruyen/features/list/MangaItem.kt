package com.vianh.blogtruyen.features.list

import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.list.items.HasUniqueId
import com.vianh.blogtruyen.features.base.list.items.ListItem

data class MangaItem(val manga: Manga, val notificationCount: Int = 0): ListItem, HasUniqueId<Int> {

    override val id: Int
        get() = manga.id

    override val viewType: Int = ListItem.SINGLE_ITEM
}