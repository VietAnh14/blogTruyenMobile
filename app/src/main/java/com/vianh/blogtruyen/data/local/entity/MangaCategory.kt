package com.vianh.blogtruyen.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "MangaCategory",
    primaryKeys = ["mangaId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["mangaId"],
            childColumns = ["mangaId"]
        ), ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"]
        )
    ]
)
data class MangaCategory(
    val mangaId: Int,
    val categoryId: Int
)