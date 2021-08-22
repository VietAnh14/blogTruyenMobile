package com.vianh.blogtruyen.features.reader

import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.list.ListItem
import com.vianh.blogtruyen.features.reader.list.ReaderItem

data class ReaderModel(
    val manga: Manga,
    val chapter: Chapter,
    val items: List<ReaderItem>
)