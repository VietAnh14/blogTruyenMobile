package com.vianh.blogtruyen.features.home.list

import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.HasUniqueId
import com.vianh.blogtruyen.features.list.ListItem

data class MangaItem(val manga: Manga): ListItem, HasUniqueId<Int> {

    override val id: Int
        get() = manga.id

    override val viewType: Int = ListItem.SINGLE_ITEM_TYPE
}