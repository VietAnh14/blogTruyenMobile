package com.vianh.blogtruyen.features.mangaDetails.data

import androidx.room.withTransaction
import com.vianh.blogtruyen.data.local.MangaDb
import com.vianh.blogtruyen.data.local.entity.CategoryEntity
import com.vianh.blogtruyen.data.local.entity.ChapterEntity
import com.vianh.blogtruyen.data.local.entity.MangaCategory
import com.vianh.blogtruyen.data.local.entity.MangaEntity
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.data.remote.MangaProvider

interface MangaRepo {
    suspend fun upsertManga(manga: Manga, updateCategories: Boolean = true)

    suspend fun findAllReadChapter(mangaId: Int): List<ChapterEntity>

    suspend fun fetchMangaDetails(manga: Manga, remote: Boolean = true): Manga

    suspend fun loadChapter(mangaId: Int, remote: Boolean = true): List<Chapter>

    suspend fun loadComments(mangaId: Int, offset: Int): Map<Comment, List<Comment>>
}

class MangaRepository(private val db: MangaDb, private val provider: MangaProvider) : MangaRepo {

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

    // TODO: USE ID
    override suspend fun fetchMangaDetails(manga: Manga, remote: Boolean): Manga {
        return if (remote) {
            val details = provider.fetchDetailManga(manga)
            upsertManga(details, true)
            details
        } else {
            db.mangaDao.getMangaById(manga.id)?.toManga()
                ?: throw IllegalStateException("Cannot found Manga")
        }
    }

    override suspend fun loadChapter(mangaId: Int, remote: Boolean): List<Chapter> {
        val readIds = db.chapterDao.findReadChapterByMangaId(mangaId)
            .map { it.id }
            .toSet()

        return provider
            .fetchChapterList(mangaId)
            .map {
                it.read = readIds.contains(it.id)
                it
            }
    }

    override suspend fun loadComments(mangaId: Int, offset: Int): Map<Comment, List<Comment>> {
        return provider.fetchComment(mangaId, offset)
    }
}