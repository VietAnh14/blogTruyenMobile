package com.vianh.blogtruyen.data.local.dao

import androidx.room.*
import com.vianh.blogtruyen.data.local.dao.BaseDao
import com.vianh.blogtruyen.data.local.entity.CategoryEntity
import com.vianh.blogtruyen.data.local.entity.MangaCategory
import com.vianh.blogtruyen.data.local.entity.MangaEntity

@Dao
abstract class MangaDao: BaseDao<MangaEntity>() {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertManga(manga: Manga)
//
//    @Query("SELECT * FROM manga WHERE subscribed = 1")
//    suspend fun observeBookmarks(): Flow<List<Manga>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertChapter(chapter: Chapter)
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertChapters(chapters: List<Chapter>)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun inserListCategory(category: List<Category>)
//
//    @Query("SELECT * FROM chapters WHERE chapters.mangaId = :mangaId")
//    suspend fun getChapters(mangaId: Int): MutableList<Chapter>
//
//    @Transaction
//    @Query("SELECT * FROM manga WHERE manga.mangaId = :mangaId")
//    suspend fun getMangaWithCategories(mangaId: Int): MangaWithCategories
//
//    @Query("SELECT * FROM chapters WHERE chapters.mangaId = :mangaId AND chapters.isRead = 1")
//    suspend fun getChaptersRead(mangaId: Int): List<Chapter>
//
//    @Update
//    suspend fun updateChapter(chapter: Chapter)

    @Query("SELECT * FROM manga WHERE mangaId = :id")
    abstract suspend fun getMangaById(id: Int): MangaEntity?

    @Query("DELETE FROM mangacategory WHERE mangaId = :mangaId")
    abstract suspend fun deleteCategoryRelation(mangaId: Int)

    @Insert
    abstract suspend fun insertCategoryRelation(relations: Collection<MangaCategory>)
}