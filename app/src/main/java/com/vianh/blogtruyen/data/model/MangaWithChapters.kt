package com.vianh.blogtruyen.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class MangaWithChapters(
    @Embedded
    val manga: Manga,
    @Relation(
        parentColumn = "id",
        entityColumn = "mangaId"
    )
    val chapter: List<Chapter>
)