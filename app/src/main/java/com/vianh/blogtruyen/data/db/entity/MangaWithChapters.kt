package com.vianh.blogtruyen.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MangaWithChapters(
    @Embedded
    val manga: MangaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "mangaId"
    )
    val chapter: List<ChapterEntity>
)