package com.vianh.blogtruyen.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.vianh.blogtruyen.data.model.Chapter

//TODO: ADD INDEX
@Entity(
    tableName = ChapterEntity.TABLE_NAME,
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
    val uploadDate: Long,
    val isRead: Boolean = false
) {
    fun toChapter(): Chapter {
        return Chapter(
            id = id,
            url = url,
            name = name,
            number = number,
            uploadDate = uploadDate,
            read = isRead
        )
    }

    companion object {
        const val TABLE_NAME = "chapters"
        fun fromChapter(chapter: Chapter, mangaId: Int): ChapterEntity {
            return ChapterEntity(
                url = chapter.url,
                name = chapter.name,
                id = chapter.id,
                mangaId = mangaId,
                isRead = chapter.read,
                number = chapter.number,
                uploadDate = chapter.uploadDate
            )
        }
    }
}