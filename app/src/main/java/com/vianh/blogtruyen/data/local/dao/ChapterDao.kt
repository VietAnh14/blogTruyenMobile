package com.vianh.blogtruyen.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.vianh.blogtruyen.data.local.dao.BaseDao
import com.vianh.blogtruyen.data.local.entity.ChapterEntity
import com.vianh.blogtruyen.data.model.Chapter

@Dao
abstract class ChapterDao: BaseDao<ChapterEntity>() {

    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId AND isRead = 1")
    abstract suspend fun findReadChapterByMangaId(mangaId: Int): List<ChapterEntity>

    @Query("SELECT * FROM chapters WHERE chapters.id = :mangaId")
    abstract suspend fun findChapterById(mangaId: String): ChapterEntity?
}