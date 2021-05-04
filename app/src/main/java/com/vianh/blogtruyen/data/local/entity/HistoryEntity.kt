package com.vianh.blogtruyen.data.local.entity

import androidx.room.*

@Entity(
    tableName = "history",
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["mangaId"],
            childColumns = ["mangaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mangaId", "chapterId"])
    ]
)
data class HistoryEntity(
    val mangaId: Int,
    val chapterId: String,
    val lastRead: Long,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)