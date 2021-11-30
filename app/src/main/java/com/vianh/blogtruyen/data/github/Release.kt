package com.vianh.blogtruyen.data.github

import java.util.*


data class Release(
    val releaseUrl: String,
    val downloadUrl: String,
    val name: String,
    val releaseSize: Long,
    val createAt: Date?
) {

    val versionCode = kotlin.runCatching { name.filter { it.isDigit() }.toInt() }.getOrDefault(0)
}