package com.vianh.blogtruyen.features.home.list

import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.list.ListItem

data class MangaItem(
    val manga: Manga, override val viewType: Int = ListItem.MANGA_ITEM
): ListItem