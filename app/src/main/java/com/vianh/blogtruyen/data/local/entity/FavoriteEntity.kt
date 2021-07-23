package com.vianh.blogtruyen.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.vianh.blogtruyen.data.model.Favorite
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
    val createdTime: Long,
    val currentChapterCount: Int,
    val newChapterCount: Int = 0
) {

    companion object {

        fun fromManga(manga: Manga): FavoriteEntity {
            return FavoriteEntity(
                sourceMangaId = manga.id,
                createdTime = System.currentTimeMillis(),
                currentChapterCount = manga.chapters.size,
                newChapterCount = 0
            )
        }

        fun fromFavorite(favorite: Favorite): FavoriteEntity {
            return FavoriteEntity(
                sourceMangaId = favorite.manga.id,
                createdTime = favorite.subscribeTime,
                currentChapterCount = favorite.currentChapterCount,
                newChapterCount = favorite.newChapterCount
            )
        }
    }
}