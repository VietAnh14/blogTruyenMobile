package com.vianh.blogtruyen.data.model

data class Chapter(
    val id: String,
    val url: String,
    val name: String,
    val mangaId: String,
    val pages: List<String> = listOf(),
    val read: Boolean = false
)