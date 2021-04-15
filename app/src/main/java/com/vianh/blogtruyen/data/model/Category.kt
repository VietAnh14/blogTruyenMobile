package com.vianh.blogtruyen.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val id: String,
    val name: String,
    val query: String
): Parcelable