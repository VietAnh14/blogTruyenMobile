package com.vianh.blogtruyen.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class FullHistory(
    @Embedded
    val history: HistoryEntity,

    @Relation(
        parentColumn = "mangaId",
        entityColumn = "mangaId"
    )
    val manga: MangaEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "chapterId"
    )
    val lastReadChapter: ChapterEntity
)