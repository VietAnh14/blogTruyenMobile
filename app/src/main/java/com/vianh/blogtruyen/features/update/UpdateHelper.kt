package com.vianh.blogtruyen.features.update

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.github.Release
import com.vianh.blogtruyen.utils.NotificationHelper
import com.vianh.blogtruyen.utils.PendingIntentHelper

class UpdateHelper {

    companion object {
        val NOTIFICATION_CHANNEL_NAME = "update channel"

        fun createNotificationChannel(context: Context) {
            NotificationHelper.createNotificationChannel(context, NOTIFICATION_CHANNEL_NAME, NOTIFICATION_CHANNEL_NAME)
        }

        @SuppressLint("LaunchActivityFromNotification")
        fun sendUpdateNotification(context: Context, release: Release) {

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(release.releaseUrl))

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntentHelper.getPendingFlagCompat())

            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_NAME)
                .setContentTitle("Update available")
                .setContentText("Version ${release.name} is available")
                .setSmallIcon(R.drawable.ic_arrow_circle_up)
                .setContentIntent(pendingIntent)
                .build()

            NotificationManagerCompat.from(context).notify(release.name.hashCode(), notification)
        }
    }
}