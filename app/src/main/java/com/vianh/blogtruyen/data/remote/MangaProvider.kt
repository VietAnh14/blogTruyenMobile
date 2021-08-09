package com.vianh.blogtruyen.data.remote

import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.Manga

interface MangaProvider {
    suspend fun fetchNewManga(pageNumber: Int): List<Manga>
    suspend fun fetchDetailManga(manga: Manga): Manga
    suspend fun fetchChapterList(mangaId: Int): List<Chapter>
    suspend fun fetchChapterPage(link: String): List<String>
    suspend fun fetchComment(mangaId: Int, offset: Int): Map<Comment, List<Comment>>
    suspend fun fetchNewFeed(): FeedItem
}