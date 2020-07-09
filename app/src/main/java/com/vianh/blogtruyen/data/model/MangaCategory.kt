package com.vianh.blogtruyen.data.model

import androidx.room.Entity

@Entity(
    tableName = "MangaCategory",
    primaryKeys = ["mangaId", "categoryId"]
)
data class MangaCategory(
    val mangaId: Int,
    val categoryId: String
)