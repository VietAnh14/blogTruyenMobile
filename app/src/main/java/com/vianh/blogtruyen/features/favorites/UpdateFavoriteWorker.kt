package com.vianh.blogtruyen.features.favorites

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Favorite
import com.vianh.blogtruyen.features.details.data.MangaRepo
import com.vianh.blogtruyen.features.download.DownloadNotificationHelper
import com.vianh.blogtruyen.features.favorites.data.FavoriteRepository
import com.vianh.blogtruyen.features.main.MainActivity
import com.vianh.blogtruyen.utils.PendingIntentHelper
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class UpdateFavoriteWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters), KoinComponent {

    private val favoriteRepository by inject<FavoriteRepository>()
    private val mangaRepository by inject<MangaRepo>()

    override suspend fun doWork(): Result {
        val favoriteMangas = favoriteRepository.observeAll().first()
        if (favoriteMangas.isEmpty())
            return Result.success()

        val notificationMessage = updateFavoriteFromRemote(favoriteMangas)
        if (notificationMessage != null) {
            sendNewUpdateNotification(notificationMessage)
        }

        return Result.success()
    }

    private fun sendNewUpdateNotification(message: String) {
        val intent = MainActivity.newIntent(applicationContext).apply {
            addFlags(PendingIntentHelper.UPDATE_INTENT_FLAGS)
            action = MainActivity.ACTION_FAVORITE_UPDATE
        }

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntentHelper.getPendingFlagCompat())

        val builder = NotificationCompat.Builder(
            applicationContext,
            DownloadNotificationHelper.NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notifications)
            .setTicker(message)
            .setContentTitle("New manga update")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//             Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    private suspend fun updateFavoriteFromRemote(favorites: List<Favorite>): String? {
        var newUpdateCount = 0

        for (favorite in favorites) {
            val manga = favorite.manga
            val remoteChapters = mangaRepository.loadChapters(manga.id, true)
            val knownChapterCount = favorite.currentChapterCount + favorite.newChapterCount
            val newChapterCount = remoteChapters.size - knownChapterCount

            if (newChapterCount > 0) {
                favoriteRepository.upsertFavorite(
                    favorite.copy(newChapterCount = newChapterCount + favorite.newChapterCount)
                )
                newUpdateCount++
            }
        }

        if (newUpdateCount > 0) {
            return "$newUpdateCount manga got new updates"
        }
        return null
    }


    companion object {

        private const val WORK_NAME = "UpdateWorker"

        fun setupUpdateWork(context: Context) {
            val constrains = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val updateWorkRequest =
                PeriodicWorkRequestBuilder<UpdateFavoriteWorker>(8, TimeUnit.HOURS)
                    .addTag(WORK_NAME)
                    .setConstraints(constrains)
                    .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    updateWorkRequest
                )
        }

        fun executeOneTime(context: Context) {
            val constrains = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val updateWorkRequest =
                OneTimeWorkRequestBuilder<UpdateFavoriteWorker>()
                    .addTag(WORK_NAME)
                    .setConstraints(constrains)
                    .build()

            WorkManager.getInstance(context).enqueue(updateWorkRequest)
        }
    }
}