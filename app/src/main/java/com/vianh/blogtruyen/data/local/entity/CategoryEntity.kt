package com.vianh.blogtruyen.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "categories",
    primaryKeys = ["categoryId"]
)
data class CategoryEntity(
    val categoryId: String,
    val displayName: String,
    val link: String
)

fun CategoryEntity.toEntity() {

}

fun CategoryEntity.fromEntity() {

}