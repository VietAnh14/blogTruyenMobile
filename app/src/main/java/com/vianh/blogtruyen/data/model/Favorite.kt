package com.vianh.blogtruyen.data.model

data class Favorite(
    val manga: Manga,
    val currentChapterCount: Int,
    val newChapterCount: Int,
    val subscribeTime: Long
)