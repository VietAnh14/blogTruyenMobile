package com.vianh.blogtruyen.data.model

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    tableName = "chapters",
    primaryKeys = ["id"]
)
data class Chapter(
    var url: String,
    var name: String,
    var id: String,
    var mangaId: Int,
    var isRead: Boolean = false): Parcelable