package com.vianh.blogtruyen.features.reader

import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.list.ListItem

data class ReaderModel(
    val manga: Manga,
    val chapter: Chapter,
    val items: List<ListItem>
)