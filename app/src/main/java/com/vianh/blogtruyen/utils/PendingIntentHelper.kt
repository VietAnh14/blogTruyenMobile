package com.vianh.blogtruyen.utils

import android.app.PendingIntent
import android.content.Intent
import android.os.Build

object PendingIntentHelper {

    const val UPDATE_INTENT_FLAGS = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

    fun getPendingFlagCompat(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
    }
}