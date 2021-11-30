package com.vianh.blogtruyen.utils

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {

    inline fun createNotificationChannel(
        context: Context,
        id: String,
        name: String,
        importance: Int = NotificationManagerCompat.IMPORTANCE_DEFAULT,
        builder: NotificationChannelCompat.Builder.() -> Unit = {  }
    ) {
        val channel = NotificationChannelCompat
            .Builder(id, importance)
            .setName(name)
            .apply { builder() }
            .build()

        // Register the channel with the system
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(channel)
    }
}