package com.vianh.blogtruyen.data.repo

import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.FeedItem
import com.vianh.blogtruyen.data.model.Manga

interface MangaProviderRepo {
    suspend fun getList(page: Int): List<Manga>
    suspend fun getMangaDetails(manga: Manga): Manga
    suspend fun getChapters(mangaId: Int): List<Chapter>
    suspend fun getChapterPages(chapter: Chapter): List<String>
    suspend fun getComment(mangaId: Int, offset: Int): Map<Comment, List<Comment>>
    suspend fun getNewFeed(): FeedItem
}