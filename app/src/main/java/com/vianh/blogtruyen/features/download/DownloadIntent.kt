package com.vianh.blogtruyen.features.download

import android.os.Parcelable
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadIntent(
    val manga: Manga,
    val chapters: List<Chapter>
): Parcelable