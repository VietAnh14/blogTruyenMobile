package com.vianh.blogtruyen.data.local

import com.vianh.blogtruyen.data.local.entity.*
import com.vianh.blogtruyen.data.model.Category
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.History
import com.vianh.blogtruyen.data.model.Manga
import kotlinx.coroutines.flow.Flow

// TODO: Split to multiple interfaces
interface DbHelper {

    suspend fun upsertManga(manga: Manga, updateCategories: Boolean = true)

    suspend fun findAllReadChapter(mangaId: Int): List<ChapterEntity>

    suspend fun markChapterAsRead(chapter: Chapter, mangaId: Int)

    fun observeHistory(): Flow<List<History>>

    suspend fun clearAllHistory()

    suspend fun deleteHistory(history: History)
}