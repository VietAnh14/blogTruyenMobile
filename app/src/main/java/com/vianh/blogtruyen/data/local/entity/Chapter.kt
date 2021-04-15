package com.vianh.blogtruyen.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    tableName = "chapters",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = Manga::class,
            parentColumns = ["mangaId"],
            childColumns = ["mangaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Chapter(
    var url: String,
    var name: String,
    var id: String,
    var mangaId: Int,
    var isRead: Boolean = false): Parcelable