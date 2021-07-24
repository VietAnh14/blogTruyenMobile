package com.vianh.blogtruyen.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
): Parcelable {

    companion object {
        const val EMPTY_ID = -1
        fun getEmpty(): Manga {
            return Manga(
                id = EMPTY_ID,
                imageUrl = "Empty image",
                title = "Manga title",
                uploadTitle = "Upload title",
                description = "Updating",
                link = "Empty link"
            )
        }
    }
}