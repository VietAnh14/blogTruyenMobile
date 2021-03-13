package com.vianh.blogtruyen.ui.home

import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.ui.list.ListItem

data class MangaItem(
    val manga: Manga, override val viewType: Int = ListItem.MANGA_ITEM
): ListItem