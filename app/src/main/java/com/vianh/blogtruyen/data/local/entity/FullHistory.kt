package com.vianh.blogtruyen.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.vianh.blogtruyen.data.model.History

data class FullHistory(
    @Embedded
    val history: HistoryEntity,

    @Relation(
        parentColumn = "mangaId",
        entityColumn = "mangaId"
    )
    val manga: MangaEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "chapterId"
    )
    val lastReadChapter: ChapterEntity
) {

    fun toHistory(): History {
        return History(
            manga = manga.toManga(),
            chapter = lastReadChapter.toChapter(),
            lastRead = history.lastRead
        )
    }
}