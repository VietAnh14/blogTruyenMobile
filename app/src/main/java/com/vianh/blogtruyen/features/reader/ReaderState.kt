package com.vianh.blogtruyen.features.reader

import android.os.Bundle
import android.os.Parcelable
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReaderState(
    val manga: Manga,
    val chapter: Chapter,
    val isOffline: Boolean = false
) : Parcelable {

    fun withoutChapters(): ReaderState {
        return this.copy(manga = manga.withoutChapter())
    }

    fun withChapters(chapters: List<Chapter>): ReaderState {
        val mangaWithChapters = manga.copy(chapters = chapters)
        return this.copy(manga = mangaWithChapters)
    }
}

data class ControllerState(
    val enable: Boolean = true,
    val page: Int,
    val pageSize: Int
)