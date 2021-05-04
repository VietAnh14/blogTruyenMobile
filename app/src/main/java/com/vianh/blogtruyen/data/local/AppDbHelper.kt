package com.vianh.blogtruyen.data.local

import androidx.room.withTransaction
import com.vianh.blogtruyen.data.local.entity.*
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga

class AppDbHelper(val db: MangaDb) : DbHelper {
    override suspend fun upsertManga(manga: Manga, updateCategories: Boolean) {
        db.withTransaction {
            val mangaId = manga.id
            db.mangaDao.upsert(MangaEntity.fromManga(manga))
            if (updateCategories) {
                db.mangaDao.deleteCategoryRelation(mangaId)
                val categoryEntities = manga.categories.map { CategoryEntity.fromCategory(it) }
                db.categoryDao.upsert(categoryEntities)
                val relations = categoryEntities.map { MangaCategory(mangaId, it.categoryId) }
                db.mangaDao.insertCategoryRelation(relations)
            }
        }
    }

    override suspend fun findAllReadChapter(mangaId: Int): List<ChapterEntity> {
        return db.chapterDao.findReadChapterByMangaId(mangaId)
    }

    override suspend fun markChapterAsRead(chapter: Chapter) {
        db.withTransaction {
            chapter.read = true
            db.chapterDao.upsert(ChapterEntity.fromChapter(chapter))
            db.historyDao.upsert(
                HistoryEntity(
                    mangaId = chapter.mangaId,
                    chapterId = chapter.id,
                    lastRead = System.currentTimeMillis()
                )
            )
        }
    }

//    override suspend fun insertManga(manga: Manga) {
//        db.mangaDao().insertManga(manga)
//    }
//
//    override suspend fun insertChapter(chapter: Chapter) {
//        db.mangaDao().insertChapter(chapter)
//    }
//
//    override suspend fun insertChapters(chapters: List<Chapter>) {
//        return db.mangaDao().insertChapters(chapters)
//    }
//
//    override suspend fun inserListCategory(category: List<Category>) {
//        return db.mangaDao().inserListCategory(category)
//    }
//
//    override suspend fun getChapters(mangaId: Int): MutableList<Chapter> {
//        return db.mangaDao().getChapters(mangaId)
//    }
//
//    override suspend fun getMangaWithCategories(mangaId: Int): MangaWithCategories {
//        return db.mangaDao().getMangaWithCategories(mangaId)
//    }
//
//    override suspend fun getChapterRead(mangaId: Int): List<Chapter> {
//        return db.mangaDao().getChaptersRead(mangaId)
//    }
//
//    override suspend fun updateChapter(chapter: Chapter) {
//        return db.mangaDao().updateChapter(chapter)
//    }
}