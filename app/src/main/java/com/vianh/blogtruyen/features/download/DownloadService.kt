package com.vianh.blogtruyen.features.download

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
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
        startForeground(
            DownloadNotificationHelper.NOTIFICATION_ID,
            notificationHelper.getDownloadNotification().build()
        )
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val downloadIntent: DownloadIntent? = intent?.extras?.getParcelable(DOWNLOAD_INTENT_KEY)
        if (downloadIntent == null) {
            stopSelf()
        } else {
            download(downloadIntent, startId)
        }

        return START_NOT_STICKY
    }

    fun download(downloadIntent: DownloadIntent, startId: Int) {
        val downloadState: MutableStateFlow<DownloadState> = MutableStateFlow(DownloadState.Queued)
        val manga = downloadIntent.manga
        val chapter = downloadIntent.chapter
        val downloadItem = DownloadItem(manga, chapter, downloadState)

        val downloadFlow = downloadHelper.downloadChapter(downloadItem)
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
            .onCompletion { completeDownload(downloadItem, startId) }

        downloadQueue.update {
            val new = ArrayList(it)
            new.add(Pair(downloadItem, downloadFlow))
            new
        }

        val isDownloading = downloadJob?.isActive
        if (isDownloading == null || !isDownloading) {
            downloadJob = downloadFlow.launchIn(lifecycleScope)
        }
    }

    private fun completeDownload(downloadItem: DownloadItem, startId: Int) {
        notificationHelper.sendDoneNotification(startId, downloadItem.manga.title)

        val currentDownload = downloadQueue.value.toMutableList()
        val index = currentDownload.indexOfFirst { it.first == downloadItem }
        if (index != -1) {
            currentDownload.removeAt(index)
            downloadQueue.value = currentDownload
        }

        if (downloadQueue.value.isEmpty()) {
            stopSelf()
        } else {
            downloadJob = downloadQueue.value.first().second.launchIn(lifecycleScope)
        }
    }


    companion object {

        val downloadQueue by lazy { MutableStateFlow(listOf<Pair<DownloadItem, Flow<Int>>>()) }

        private const val DOWNLOAD_INTENT_KEY = "DOWNLOAD_INTENT"

        fun start(context: Context, downloadIntent: DownloadIntent) {
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra(DOWNLOAD_INTENT_KEY, downloadIntent)
            context.startService(intent)
        }
    }
}