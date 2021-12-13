package com.vianh.blogtruyen.data.db.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation


data class MangaWithCategories(
    @Embedded
    val manga: MangaEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "mangaId"
    )
    val chapter: List<ChapterEntity>,

    @Relation(
        parentColumn = "mangaId",
        entityColumn = "categoryId",
        associateBy = Junction(MangaCategory::class)
    )
    val categories: List<CategoryEntity>
)