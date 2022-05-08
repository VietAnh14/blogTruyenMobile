package com.vianh.blogtruyen.data.repo

import com.vianh.blogtruyen.data.remote.MangaProvider
import com.vianh.blogtruyen.data.db.MangaDb
import com.vianh.blogtruyen.data.db.entity.CategoryEntity
import com.vianh.blogtruyen.data.db.entity.MangaCategory
import com.vianh.blogtruyen.data.db.entity.MangaEntity
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.FeedItem
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.utils.ext.mapToSet
import kotlinx.coroutines.flow.first

class MangaProviderRepoImpl(val provider: MangaProvider, val db: MangaDb): MangaProviderRepo {

    override suspend fun getList(page: Int): List<Manga> = provider.getMangaList(page)

    override suspend fun getMangaDetails(manga: Manga): Manga {
        val remoteManga = provider.getMangaDetails(manga)
        db.mangaDao.upsert(MangaEntity.fromManga(remoteManga))
        return remoteManga
    }

    override suspend fun getChapters(mangaId: Int): List<Chapter> {
        val readIds = db.chapterDao.observeReadChapterByMangaId(mangaId).first().mapToSet { it.id }
        val remoteChapter = provider.getChapterList(mangaId)
        for (chapter in remoteChapter) {
            chapter.read = readIds.contains(chapter.id)
        }

        return remoteChapter
    }

    override suspend fun getChapterPages(chapter: Chapter, mangaId: Int): List<String> = provider.getChapterPage(chapter)

    override suspend fun getComment(mangaId: Int, offset: Int): Map<Comment, List<Comment>> = provider.getComment(mangaId, offset)

    override suspend fun getNewFeed(): FeedItem = provider.getNewFeed()
}