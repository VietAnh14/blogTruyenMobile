package com.vianh.blogtruyen.data.db.entity

import androidx.room.Entity
import com.vianh.blogtruyen.data.model.Manga

@Entity(
    tableName = MangaEntity.TABLE_NAME,
    primaryKeys = ["mangaId"]
)
data class MangaEntity(
    val imageUrl: String,
    val link: String,
    val title: String,
    val uploadTitle: String,
    val description: String = "Still update",
    val mangaId: Int
) {

    fun toManga(): Manga {
        return Manga(
            id = mangaId,
            imageUrl = imageUrl,
            link = link,
            title = title,
            uploadTitle = uploadTitle,
            description = description
        )
    }


    companion object {
        const val TABLE_NAME = "manga"
        fun fromManga(manga: Manga):MangaEntity {
            return MangaEntity(
                imageUrl = manga.imageUrl,
                link =  manga.link,
                title = manga.title,
                uploadTitle = manga.uploadTitle,
                description = manga.description,
                mangaId = manga.id
            )
        }
    }
}