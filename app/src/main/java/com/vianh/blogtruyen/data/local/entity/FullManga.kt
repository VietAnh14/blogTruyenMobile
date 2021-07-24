package com.vianh.blogtruyen.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class FullManga(
    @Embedded
    val manga: MangaEntity,



    @Relation(
        parentColumn = "mangaId",
        entityColumn = "categoryId",
        associateBy = Junction(MangaCategory::class)
    )
    val categories: List<CategoryEntity>
)