package com.vianh.blogtruyen.data.model

data class FeedItem (
    val hotManga: List<Manga>,
    val newUpdateManga: List<Manga>,
    val newUploadManga: List<Manga>
)