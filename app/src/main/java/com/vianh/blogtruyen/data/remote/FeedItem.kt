package com.vianh.blogtruyen.data.remote

import com.vianh.blogtruyen.data.model.Manga

data class FeedItem (
    val pinStories: List<Manga>,
    val newUpdate: List<Manga>,
    val newStories: List<Manga>
)