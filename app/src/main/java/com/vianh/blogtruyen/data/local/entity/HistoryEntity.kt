package com.vianh.blogtruyen.data.local.entity

import androidx.room.*
import com.vianh.blogtruyen.data.model.History

@Entity(
    tableName = "history",
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["mangaId"],
            childColumns = ["refMangaId"],
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
        Index(value = ["refMangaId", "chapterId"])
    ]
)
data class HistoryEntity(
    @PrimaryKey
    val refMangaId: Int,
    val chapterId: String,
    val lastRead: Long
) {

    companion object {
        fun fromHistory(history: History): HistoryEntity {
            return HistoryEntity(
                refMangaId = history.manga.id,
                chapterId = history.chapter.id,
                lastRead = history.lastRead
            )
        }
    }
}