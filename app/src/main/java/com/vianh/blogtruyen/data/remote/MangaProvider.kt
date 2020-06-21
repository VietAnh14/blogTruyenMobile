package com.vianh.blogtruyen.data.remote

import com.vianh.blogtruyen.data.PreviewManga
import kotlinx.coroutines.Deferred

interface MangaProvider {
    suspend fun fetchNewManga(): List<PreviewManga>
}