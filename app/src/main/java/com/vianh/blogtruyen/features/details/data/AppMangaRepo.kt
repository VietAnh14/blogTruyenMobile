package com.vianh.blogtruyen.features.details.data

import androidx.room.withTransaction
import com.vianh.blogtruyen.data.db.MangaDb
import com.vianh.blogtruyen.data.db.entity.*
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.utils.ext.mapList
import kotlinx.coroutines.flow.Flow

interface AppMangaRepo {
    suspend fun upsertManga(manga: Manga, updateCategories: Boolean = true)

    suspend fun findMangaById(mangaId: Int): Manga?

    suspend fun markChapterAsRead(chapter: Chapter, mangaId: Int)

    fun observeChapter(mangaId: Int): Flow<List<Chapter>>
}

class AppMangaRepository(private val db: MangaDb) : AppMangaRepo {

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

    override suspend fun findMangaById(mangaId: Int): Manga? {
        return db.mangaDao.getFullMangaById(mangaId)?.toManga()
    }

    override suspend fun markChapterAsRead(chapter: Chapter, mangaId: Int) {
        db.withTransaction {
            chapter.read = true
            db.chapterDao.upsert(ChapterEntity.fromChapter(chapter, mangaId))
            db.historyDao.upsert(
                HistoryEntity(
                    refMangaId = mangaId,
                    chapterId = chapter.id,
                    lastRead = System.currentTimeMillis()
                )
            )
        }
    }

    override fun observeChapter(mangaId: Int): Flow<List<Chapter>> {
        return db.chapterDao.observeChaptersByMangaId(mangaId).mapList { it.toChapter() }
    }
}