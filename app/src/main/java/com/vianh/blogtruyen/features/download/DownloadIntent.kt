package com.vianh.blogtruyen.features.download

import android.os.Parcelable
import com.vianh.blogtruyen.data.model.Chapter
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadIntent(
    val mangaTitle: String,
    val mangaId: Int,
    val coverUrl: String,
    val chapters: List<Chapter>
): Parcelable