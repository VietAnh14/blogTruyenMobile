package com.vianh.blogtruyen.data.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "MangaCategory",
    primaryKeys = ["mangaId", "categoryId"],
    indices = [Index("categoryId")]
)
data class MangaCategory(
    val mangaId: Int,
    val categoryId: String
)