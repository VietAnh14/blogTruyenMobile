package com.vianh.blogtruyen.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.vianh.blogtruyen.data.model.Favorite

data class FavoriteWithManga(

    @Relation(
        parentColumn = "sourceMangaId",
        entityColumn = "mangaId"
    )
    val manga: MangaEntity,

    @Embedded
    val favorite: FavoriteEntity
) {

    fun toFavorite(): Favorite {
        return Favorite(
            manga = manga.toManga(),
            currentChapterCount = favorite.currentChapterCount,
            newChapterCount = favorite.newChapterCount,
            subscribeTime = favorite.createdTime
        )
    }
}