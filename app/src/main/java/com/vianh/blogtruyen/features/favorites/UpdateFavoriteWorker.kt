package com.vianh.blogtruyen.features.favorites

import android.content.Context
import androidx.work.*
import com.vianh.blogtruyen.data.model.Favorite
import com.vianh.blogtruyen.features.favorites.data.FavoriteRepository
import com.vianh.blogtruyen.features.mangaDetails.data.MangaRepo
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class UpdateFavoriteWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters), KoinComponent {

    private val favoriteRepository by inject<FavoriteRepository>()
    private val mangaRepository by inject<MangaRepo>()

    override suspend fun doWork(): Result {
        Timber.d("Updating favorite mangas")
        val favoriteMangas = favoriteRepository.observeFavorite().first()
        if (favoriteMangas.isEmpty())
            return Result.success()

        for (favorite in favoriteMangas) {
            updateFavoriteFromRemote(favorite)
        }

        Timber.d("Done updating favorite mangas")

        return Result.success()
    }

    private suspend fun updateFavoriteFromRemote(favorite: Favorite) {
        val manga = favorite.manga
        val remoteChapters = mangaRepository.loadChapter(manga.id, true)
        val knownChapterCount = favorite.currentChapterCount + favorite.newChapterCount
        val newChapterCount = remoteChapters.size - knownChapterCount

        if (newChapterCount > 0) {
            favoriteRepository.upsertFavorite(
                favorite.copy(newChapterCount = newChapterCount + favorite.newChapterCount)
            )
        }
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
    }
}