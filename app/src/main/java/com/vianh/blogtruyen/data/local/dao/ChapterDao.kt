package com.vianh.blogtruyen.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.vianh.blogtruyen.data.local.dao.BaseDao
import com.vianh.blogtruyen.data.local.entity.ChapterEntity

@Dao
abstract class ChapterDao: BaseDao<ChapterEntity>() {

    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId AND isRead = 1")
    abstract suspend fun findReadChapterByMangaId(mangaId: Int): List<ChapterEntity>
}