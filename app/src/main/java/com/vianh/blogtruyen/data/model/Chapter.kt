package com.vianh.blogtruyen.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chapter(
    val id: String,
    val url: String,
    val name: String,
    val mangaId: String,
    val pages: List<String> = listOf(),
    val read: Boolean = false
): Parcelable