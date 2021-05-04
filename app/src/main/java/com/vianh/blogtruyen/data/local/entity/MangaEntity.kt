package com.vianh.blogtruyen.data.local.entity

import androidx.room.Entity
import com.vianh.blogtruyen.data.model.Manga

@Entity(
    tableName = "manga",
    primaryKeys = ["mangaId"]
)
data class MangaEntity(
    val imageUrl: String,
    val link: String,
    val title: String,
    val uploadTitle: String,
    val description: String = "Still update",
    val mangaId: Int,
    val subscribed: Boolean = false
) {

    fun toManga(): Manga {
        return Manga(
            id = mangaId,
            imageUrl = imageUrl,
            link = link,
            title = title,
            uploadTitle = uploadTitle,
            description = description,
            subscribed = subscribed
        )
    }


    companion object {
        fun fromManga(manga: Manga):MangaEntity {
            return MangaEntity(
                imageUrl = manga.imageUrl,
                link =  manga.link,
                title = manga.title,
                uploadTitle = manga.uploadTitle,
                description = manga.description,
                mangaId = manga.id,
                subscribed = manga.subscribed
            )
        }
    }
}