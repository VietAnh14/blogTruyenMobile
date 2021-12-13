package com.vianh.blogtruyen.data.db

import androidx.room.withTransaction
import com.vianh.blogtruyen.data.db.entity.*
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.History
import com.vianh.blogtruyen.data.model.Manga
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

// TODO: Inject dispatchers
class AppDbHelper(private val db: MangaDb) : DbHelper {
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

    override fun observeHistory(): Flow<List<History>> {
        return db.historyDao
            .observeFullHistory()
            .map {
                it.map { fullHistory -> fullHistory.toHistory() }
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun clearAllHistory() {
        return db.historyDao.deleteAll()
    }

    override suspend fun deleteHistory(history: History) {
        return db.historyDao.delete(HistoryEntity.fromHistory(history))
    }
}