package com.vianh.blogtruyen.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.utils.mapToSet

data class FullManga(
    @Embedded
    val manga: MangaEntity,



    @Relation(
        parentColumn = "mangaId",
        entityColumn = "categoryId",
        associateBy = Junction(MangaCategory::class)
    )
    val categories: List<CategoryEntity>
) {

    fun toManga(): Manga {
        return Manga(
            id = manga.mangaId,
            imageUrl = manga.imageUrl,
            title = manga.title,
            uploadTitle = manga.uploadTitle,
            description = manga.description,
            link = manga.link,
            categories = categories.mapToSet { it.toCategory() }
        )
    }
}