package com.vianh.blogtruyen.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.vianh.blogtruyen.data.local.dao.BaseDao
import com.vianh.blogtruyen.data.local.entity.ChapterEntity
import com.vianh.blogtruyen.data.model.Chapter
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ChapterDao: BaseDao<ChapterEntity>() {

    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId AND isRead = 1")
    abstract fun observeReadChapterByMangaId(mangaId: Int): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE chapters.id = :chapterId")
    abstract suspend fun findChapterById(chapterId: String): ChapterEntity?

    @Query("SELECT * FROM chapters WHERE chapters.mangaId = :mangaId")
    abstract fun observeChaptersByMangaId(mangaId: Int): Flow<List<ChapterEntity>>
}