package com.vianh.blogtruyen.data.local

import androidx.room.*
import com.vianh.blogtruyen.data.model.Category
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.data.model.MangaWithCategories

@Dao
interface MangaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertManga(manga: Manga)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChapters(chapters: List<Chapter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserListCategory(category: List<Category>)

    @Query("SELECT * FROM chapters WHERE chapters.mangaId = :mangaId")
    suspend fun getChapters(mangaId: Int): MutableList<Chapter>

    @Transaction
    @Query("SELECT * FROM manga WHERE manga.mangaId = :mangaId")
    suspend fun getMangaWithCategories(mangaId: Int): MangaWithCategories

    @Query("SELECT * FROM chapters WHERE chapters.mangaId = :mangaId AND chapters.isRead = 1")
    suspend fun getChaptersRead(mangaId: Int): List<Chapter>

    @Update
    suspend fun updateChapter(chapter: Chapter)
}