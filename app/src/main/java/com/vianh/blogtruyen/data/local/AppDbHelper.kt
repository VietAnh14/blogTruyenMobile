package com.vianh.blogtruyen.data.local

import com.vianh.blogtruyen.data.model.Category
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.data.model.MangaWithCategories

object AppDbHelper: DbHelper {

    private val db by lazy { MangaDb.db }

    override suspend fun insertManga(manga: Manga) {
        db.mangaDao().insertManga(manga)
    }

    override suspend fun insertChapter(chapter: Chapter) {
        db.mangaDao().insertChapter(chapter)
    }

    override suspend fun insertChapters(chapters: List<Chapter>) {
        return db.mangaDao().insertChapters(chapters)
    }

    override suspend fun inserListCategory(category: List<Category>) {
        return db.mangaDao().inserListCategory(category)
    }

    override suspend fun getChapters(mangaId: Int): MutableList<Chapter> {
        return db.mangaDao().getChapters(mangaId)
    }

    override suspend fun getMangaWithCategories(mangaId: Int): MangaWithCategories {
        return db.mangaDao().getMangaWithCategories(mangaId)
    }

    override suspend fun getChapterRead(mangaId: Int): List<Chapter> {
        return db.mangaDao().getChaptersRead(mangaId)
    }

    override suspend fun updateChapter(chapter: Chapter) {
        return db.mangaDao().updateChapter(chapter)
    }
}