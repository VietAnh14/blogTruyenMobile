package com.vianh.blogtruyen.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val name: String,
    val query: String
): Parcelable