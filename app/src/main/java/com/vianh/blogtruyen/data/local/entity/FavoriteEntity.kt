package com.vianh.blogtruyen.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.vianh.blogtruyen.data.model.Manga

@Entity(
    tableName = "favorites",
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["mangaId"],
            childColumns = ["sourceMangaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FavoriteEntity(
    @PrimaryKey
    val sourceMangaId: Int,
    val lastCheck: Long,
    val currentChapterCount: Int,
    val newChapterCount: Int = 0
) {

    companion object {

        fun fromManga(manga: Manga): FavoriteEntity {
            return FavoriteEntity(
                sourceMangaId = manga.id,
                lastCheck = System.currentTimeMillis(),
                currentChapterCount = manga.chapters.size,
                newChapterCount = 0
            )
        }
    }
}