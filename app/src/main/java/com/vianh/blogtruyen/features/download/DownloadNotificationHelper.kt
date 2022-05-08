package com.vianh.blogtruyen.features.download

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.details.ui.MangaDetailsFragment
import com.vianh.blogtruyen.utils.NotificationHelper

class DownloadNotificationHelper(private val context: Context) {

    val builder by lazy { getDownloadNotification() }
    private val notificationManager by lazy { NotificationManagerCompat.from(context) }


    fun getDownloadNotification(): NotificationCompat.Builder {

        val title = context.resources.getString(R.string.app_name)

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setTicker(title)
            .setOnlyAlertOnce(true)
            .setContentTitle("Downloading")
            .setContentText("Processing")
            .setProgress(100, 0, false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
//            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
    }

    fun sendDoneNotification(id: Int, manga: Manga) {
        builder.setProgress(0, 0, false)
            .setContentText("Downloaded ${manga.title}")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentIntent(MangaDetailsFragment.getPendingIntent(context, manga, true))
            .setAutoCancel(true)

        notificationManager.notify(id, builder.build())
    }

    fun updateProgress(progress: Int) {
        val notification = builder
            .setProgress(100, progress, false)
            .build()

        updateForegroundNotification(notification)
    }

    fun updateForegroundNotification(notification: Notification) {
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION CHANNEL"
        private const val CHANNEL_NAME = "BLOG NOTIFICATION"

        fun createNotificationChannel(context: Context) {
            NotificationHelper.createNotificationChannel(context, NOTIFICATION_CHANNEL_ID, CHANNEL_NAME) {
                setDescription("Blogtruyen download service")
            }
        }
    }
}