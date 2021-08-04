package com.vianh.blogtruyen.features.mangaDetails.mangaInfo.adapter

import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.features.download.DownloadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ChapterItem(
    val chapter: Chapter,
    val downloadState: StateFlow<DownloadState> = MutableStateFlow(DownloadState.NotDownloaded)
)