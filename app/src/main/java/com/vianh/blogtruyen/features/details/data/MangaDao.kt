package com.vianh.blogtruyen.features.details.data

import androidx.room.*
import com.vianh.blogtruyen.data.local.dao.BaseDao
import com.vianh.blogtruyen.data.local.entity.FullManga
import com.vianh.blogtruyen.data.local.entity.MangaCategory
import com.vianh.blogtruyen.data.local.entity.MangaEntity

@Dao
abstract class MangaDao: BaseDao<MangaEntity>() {

    @Query("SELECT * FROM manga WHERE mangaId = :id")
    abstract suspend fun getMangaById(id: Int): MangaEntity?

    @Transaction
    @Query("SELECT * FROM manga WHERE mangaId = :id")
    abstract suspend fun getFullMangaById(id: Int): FullManga?

    @Query("DELETE FROM MangaCategory WHERE mangaId = :mangaId")
    abstract suspend fun deleteCategoryRelation(mangaId: Int)

    @Insert
    abstract suspend fun insertCategoryRelation(relations: Collection<MangaCategory>)
}