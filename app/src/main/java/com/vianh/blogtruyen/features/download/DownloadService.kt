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

    private val downloadQueue = MutableStateFlow(listOf<Pair<DownloadItem, Flow<Int>>>())

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
        val downloadItem = DownloadItem(
            startId,
            downloadIntent.mangaId,
            downloadIntent.coverUrl,
            downloadIntent.mangaTitle,
            downloadState,
            downloadIntent.chapters
        )

        val downloadFlow = downloadHelper.downloadManga(downloadItem)
            .distinctUntilChanged()
            .debounce(100)
            .onStart {
                val cover = Glide.with(this@DownloadService)
                    .asBitmap()
                    .await(downloadIntent.coverUrl)

                val startNotification = notificationHelper.builder
                    .setProgress(100, 0, false)
                    .setLargeIcon(cover)
                    .setContentText(downloadIntent.mangaTitle)
                    .build()

                notificationHelper.updateForegroundNotification(startNotification)
            }
            .onEach {
                notificationHelper.updateProgress(it)
                downloadState.value = DownloadState.InProgress(it)
            }
            .onCompletion { completeDownload(downloadItem) }

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

    private fun completeDownload(downloadItem: DownloadItem) {
        notificationHelper.sendDoneNotification(downloadItem.id, downloadItem.name)

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

        private const val DOWNLOAD_INTENT_KEY = "DOWNLOAD_INTENT"

        fun start(context: Context, downloadIntent: DownloadIntent) {
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra(DOWNLOAD_INTENT_KEY, downloadIntent)
            context.startService(intent)
        }
    }
}