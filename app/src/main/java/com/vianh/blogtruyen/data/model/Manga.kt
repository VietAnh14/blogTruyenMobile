package com.vianh.blogtruyen.data.model

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    tableName = "manga",
    primaryKeys = ["mangaId"]
)
data class Manga(
    var imageUrl: String,
    var link: String,
    var title: String,
    var uploadTitle: String,
    var description: String = "Still update",
    var mangaId: Int,
    var subscribed: Boolean = false): Parcelable