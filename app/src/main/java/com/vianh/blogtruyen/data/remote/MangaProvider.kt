package com.vianh.blogtruyen.data.remote

import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga

interface MangaProvider {
    suspend fun fetchNewManga(): List<Manga>
    suspend fun fetchDetailManga(manga: Manga): Manga
    suspend fun fetchChapterList(manga: Manga): List<Chapter>
}