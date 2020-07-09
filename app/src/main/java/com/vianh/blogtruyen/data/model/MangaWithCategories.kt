package com.vianh.blogtruyen.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation


data class MangaWithCategories(
    @Embedded
    val manga: Manga,
    @Relation(
        parentColumn = "mangaId",
        entityColumn = "categoryId",
        associateBy = Junction(MangaCategory::class)
    )
    val categories: List<Category>
)