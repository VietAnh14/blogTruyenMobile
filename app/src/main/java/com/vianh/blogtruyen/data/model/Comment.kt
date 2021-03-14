package com.vianh.blogtruyen.data.model

data class Comment(
    val userName: String,
    val avatar: String,
    val message: String,
    val time: String,
    val type: Int = TOP
) {
    companion object {
        const val TOP = 1
        const val REPLY = 2
    }
}