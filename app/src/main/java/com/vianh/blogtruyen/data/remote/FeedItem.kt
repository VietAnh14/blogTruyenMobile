package com.vianh.blogtruyen.data.remote

import com.vianh.blogtruyen.data.model.Manga

data class FeedItem (
    val hotManga: List<Manga>,
    val newUpdateManga: List<Manga>,
    val newUploadManga: List<Manga>
)