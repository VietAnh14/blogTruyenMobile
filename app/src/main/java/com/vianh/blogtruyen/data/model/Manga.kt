package com.vianh.blogtruyen.data.model

data class Manga(
    val id: Int,
    val imageUrl: String,
    val title: String,
    val uploadTitle: String,
    val description: String,
    val link: String,
    val subscribed: Boolean = false,
    val categories: Set<Category> = setOf(),
    val chapters: List<Chapter> = listOf()
)