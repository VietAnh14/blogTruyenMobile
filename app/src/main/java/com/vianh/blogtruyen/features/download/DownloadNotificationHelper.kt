package com.vianh.blogtruyen.features.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Manga

class DownloadNotificationHelper(private val context: Context) {

    val builder by lazy { getDownloadNotification() }
    private val notificationManager by lazy { NotificationManagerCompat.from(context) }


    fun getDownloadNotification(): NotificationCompat.Builder {

        val title = context.resources.getString(R.string.app_name)

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setLargeIcon((ContextCompat.getDrawable(context, R.drawable.lazy_corgi) as BitmapDrawable).bitmap)
            .setTicker(title)
            .setOnlyAlertOnce(true)
            .setContentTitle(title)
            .setContentText("Downloading")
            .setProgress(100, 0, false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
//            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
    }

    fun sendDoneNotification(id: Int, title: String) {
        builder.setProgress(0, 0, false)
        builder.setContentText("Downloaded $title")
        builder.setSmallIcon(android.R.drawable.stat_sys_download_done)
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
        private const val CHANNEL_NAME = "DOWNLOAD CHANNEL"
        private const val NOTIFICATION_CHANNEL_ID = "DOWNLOAD"

        fun createNotificationChannel(context: Context) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel =
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance).apply {
                        description = "Blogtruyen download service"
                    }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}