package com.vianh.blogtruyen.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    tableName = "categories",
    primaryKeys = ["categoryId"]
)
data class Category(
    val categoryId: String,
    val displayName: String,
    val link: String
): Parcelable

fun Category.toEntity() {

}

fun Category.fromEntity() {

}