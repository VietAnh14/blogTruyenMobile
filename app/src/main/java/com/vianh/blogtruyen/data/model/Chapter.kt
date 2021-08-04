package com.vianh.blogtruyen.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chapter(
    val id: String,
    val url: String,
    val name: String,
    val number: Int,
    val pages: List<String> = listOf(),
    var read: Boolean = false
): Parcelable