package com.vianh.blogtruyen.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.vianh.blogtruyen.data.model.Chapter

//TODO: ADD INDEX
@Entity(
    tableName = "chapters",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["mangaId"],
            childColumns = ["mangaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChapterEntity(
    val url: String,
    val name: String,
    val id: String,
    val mangaId: Int,
    val number: Int,
    val isRead: Boolean = false
) {
    fun toChapter(): Chapter {
        return Chapter(
            id = id,
            url = url,
            name = name,
            number = number,
            read = isRead
        )
    }

    companion object {
        fun fromChapter(chapter: Chapter, mangaId: Int): ChapterEntity {
            return ChapterEntity(
                url = chapter.url,
                name = chapter.name,
                id =  chapter.id,
                mangaId = mangaId,
                isRead = chapter.read,
                number = chapter.number
            )
        }
    }
}