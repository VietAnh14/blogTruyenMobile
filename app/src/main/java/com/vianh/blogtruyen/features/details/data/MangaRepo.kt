package com.vianh.blogtruyen.features.details.data

import androidx.room.withTransaction
import com.vianh.blogtruyen.data.db.MangaDb
import com.vianh.blogtruyen.data.db.entity.*
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.data.remote.MangaProvider
import com.vianh.blogtruyen.utils.ext.mapList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface MangaRepo {
    suspend fun upsertManga(manga: Manga, updateCategories: Boolean = true)

    suspend fun fetchMangaDetails(manga: Manga, remote: Boolean = true): Manga

    suspend fun loadChapters(mangaId: Int, remote: Boolean = true): List<Chapter>

    suspend fun loadComments(mangaId: Int, offset: Int): Map<Comment, List<Comment>>

    suspend fun markChapterAsRead(chapter: Chapter, mangaId: Int)

    fun observeChapter(mangaId: Int): Flow<List<Chapter>>
}

class MangaRepository(
    private val db: MangaDb,
    private val provider: MangaProvider
) : MangaRepo {

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

    // TODO: USE ID
    override suspend fun fetchMangaDetails(manga: Manga, remote: Boolean): Manga {
        return if (remote) {
            val details = provider.fetchDetailManga(manga)
            upsertManga(details, true)
            details
        } else {
            db.mangaDao.getFullMangaById(manga.id)?.toManga()
                ?: throw IllegalStateException("Cannot found Manga")
        }
    }

    override suspend fun loadChapters(mangaId: Int, remote: Boolean): List<Chapter> {
        if (remote) {
            val readIds = db.chapterDao.observeReadChapterByMangaId(mangaId)
                .mapList { it.id }
                .first()
                .toSet()

            return provider
                .fetchChapterList(mangaId)
                .map {
                    it.read = readIds.contains(it.id)
                    it
                }
        }

        return observeChapter(mangaId).first()
    }

    override suspend fun loadComments(mangaId: Int, offset: Int): Map<Comment, List<Comment>> {
        return provider.fetchComment(mangaId, offset)
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