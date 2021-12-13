package com.vianh.blogtruyen.features.details.info.adapter

import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.features.download.DownloadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

data class ChapterItem(
    val chapter: Chapter,
    val downloadState: StateFlow<DownloadState> = MutableStateFlow(DownloadState.NotDownloaded)
) {
    val uploadTimeString = getDateString()

    private fun getDateString(): String {
        val date = Date(chapter.uploadDate)
        return SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(date)
    }
}