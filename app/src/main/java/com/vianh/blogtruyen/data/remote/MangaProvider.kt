package com.vianh.blogtruyen.data.remote

import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.data.remote.FeedItem

interface MangaProvider {
    val baseUrl: String

    suspend fun getMangaList(pageNumber: Int): List<Manga>
    suspend fun getMangaDetails(manga: Manga): Manga
    suspend fun getChapterList(mangaId: Int): List<Chapter>
    suspend fun getChapterPage(link: String): List<String>
    suspend fun getComment(mangaId: Int, offset: Int): Map<Comment, List<Comment>>
    suspend fun getNewFeed(): FeedItem
    suspend fun searchByName(query: String, pageNumber: Int): List<Manga>
}