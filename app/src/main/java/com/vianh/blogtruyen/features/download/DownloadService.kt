package com.vianh.blogtruyen.features.download

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.utils.await
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class DownloadService : LifecycleService() {

    private val notificationHelper by lazy { DownloadNotificationHelper(this) }

    private val downloadHelper by inject<DownloadHelper>()

    private var downloadJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(
            DownloadNotificationHelper.NOTIFICATION_ID,
            notificationHelper.getDownloadNotification().build()
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val downloadIntent: DownloadIntent? = intent?.extras?.getParcelable(DOWNLOAD_INTENT_KEY)
        if (downloadIntent == null) {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        for (chapter in downloadIntent.chapters) {
            download(downloadIntent.manga, chapter)
        }

        return START_NOT_STICKY
    }

    fun download(manga: Manga, chapter: Chapter) {
        if (isChapterInQueue(chapter)) {
            return
        }

        val downloadState: MutableStateFlow<DownloadState> = MutableStateFlow(DownloadState.Queued)
        val downloadItem = DownloadItem(manga, chapter, downloadState)

        val downloadFlow = downloadHelper.getDownloadFlow(downloadItem)
            .distinctUntilChanged()
            .debounce(100)
            .onStart {
                val cover = Glide.with(this@DownloadService)
                    .asBitmap()
                    .await(manga.imageUrl)

                val startNotification = notificationHelper.builder
                    .setProgress(100, 0, false)
                    .setLargeIcon(cover)
                    .setContentText(manga.title)
                    .build()

                notificationHelper.updateForegroundNotification(startNotification)
            }
            .onEach {
                notificationHelper.updateProgress(it)
                downloadState.value = DownloadState.InProgress(it)
            }
            .catch { Timber.e(it) }
            .onCompletion {
                downloadState.value = DownloadState.Completed
                completeDownload(downloadItem, chapter.id.hashCode())
            }

        downloadQueue.update { it + (downloadItem to downloadFlow) }

        val isDownloading = downloadJob?.isActive
        if (isDownloading == null || !isDownloading) {
            downloadJob = downloadFlow.launchIn(lifecycleScope)
        }
    }

    fun isChapterInQueue(chapter: Chapter): Boolean {
        for (item in downloadQueue.value) {
            if (item.first.chapter == chapter)
                return true
        }

        return false
    }

    private fun completeDownload(downloadItem: DownloadItem, startId: Int) {
        val currentDownload = downloadQueue.value.toMutableList()
        val index = currentDownload.indexOfFirst { it.first == downloadItem }
        if (index != -1) {
            currentDownload.removeAt(index)
            downloadQueue.value = currentDownload
        }

        if (downloadQueue.value.isEmpty()) {
            notificationHelper.sendDoneNotification(startId, downloadItem.manga)
            stopSelf()
        } else {
            downloadJob = downloadQueue.value.first().second.launchIn(lifecycleScope)
        }
    }


    companion object {

        val downloadQueue by lazy { MutableStateFlow(listOf<Pair<DownloadItem, Flow<Int>>>()) }

        private const val DOWNLOAD_INTENT_KEY = "DOWNLOAD_INTENT"

        fun download(context: Context, manga: Manga, chapters: List<Chapter>) {
            // Don't save large items to bundle
            val emptyChapterManga = manga.copy(chapters = emptyList())
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra(DOWNLOAD_INTENT_KEY, DownloadIntent(emptyChapterManga, chapters))
            context.startService(intent)
        }
    }
}