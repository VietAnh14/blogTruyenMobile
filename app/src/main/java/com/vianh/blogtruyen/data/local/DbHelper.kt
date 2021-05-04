package com.vianh.blogtruyen.data.local

import com.vianh.blogtruyen.data.local.entity.*
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga

interface DbHelper {
//    suspend fun insertManga(manga: Manga)
//
//    suspend fun insertChapter(chapter: Chapter)
//
//    suspend fun insertChapters(chapters: List<Chapter>)
//
//    suspend fun inserListCategory(category: List<Category>)
//
//    suspend fun getChapters(mangaId: Int): MutableList<Chapter>
//
//    suspend fun getMangaWithCategories(mangaId: Int): MangaWithCategories
//
//    suspend fun getChapterRead(mangaId: Int): List<Chapter>
//
//    suspend fun updateChapter(chapter: Chapter)

    suspend fun upsertManga(manga: Manga)

    suspend fun findAllReadChapter(mangaId: Int): List<ChapterEntity>

    suspend fun markChapterAsRead(chapter: Chapter)
}