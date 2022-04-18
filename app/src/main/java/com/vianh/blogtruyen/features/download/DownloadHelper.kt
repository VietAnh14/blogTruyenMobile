package com.vianh.blogtruyen.features.download

import android.content.Context
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.data.remote.MangaProvider
import com.vianh.blogtruyen.features.local.LocalSourceRepo
import com.vianh.blogtruyen.utils.ext.await
import com.vianh.blogtruyen.utils.ext.cancelableCatching
import com.vianh.blogtruyen.utils.ext.copyTo
import com.vianh.blogtruyen.utils.ext.createDirs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit


sealed class DownloadState {
    object NotDownloaded: DownloadState()
    object Queued : DownloadState()
    data class InProgress(val progress: Int) : DownloadState()
    data class Error(val cause: Throwable) : DownloadState()
    object Completed : DownloadState()
}

data class DownloadItem(
    val manga: Manga,
    val chapter: Chapter,
    val state: StateFlow<DownloadState>
)


class DownloadHelper(
    private val context: Context,
    private val client: OkHttpClient,
    private val mangaProvider: MangaProvider,
    private val localSourceRepo: LocalSourceRepo
) {

    // TODO: DOWNLOAD TO TEMP FILE WHILE PROCESSING
    fun getDownloadFlow(downloadItem: DownloadItem): Flow<Int> {
        return flow {
            val chapter = downloadItem.chapter
            val manga = downloadItem.manga

            if (canDownloadCover(manga.id)) {
                val coverFileName = "${manga.id}.${fileExtensionFromUrl(manga.imageUrl)}"

                val cover = Glide.with(context).asFile().await(manga.imageUrl)
                cover?.copyTo(File(localSourceRepo.getCoverDir(), coverFileName), true)
            }

            val pages = mangaProvider.fetchChapterPage(chapter.url)
            var successPage = 0
            val chapterDir = localSourceRepo
                .getChapterDir(manga.id, manga.title, chapter)
                .createDirs()

            pages.forEachIndexed { index, page ->
                val request = Request.Builder().url(page).build()
                val fileName = PAGE_FORMAT.format(index, fileExtensionFromUrl(page))

                cancelableCatching {
                    val response = client.newCall(request).await()
                    val downloadedImage = File(chapterDir, fileName)
                    response.copyTo(downloadedImage)
                }.onFailure { err ->
                    Timber.e(err)
                }

                successPage++
                emit((successPage * 100f / pages.size).toInt())
            }

            localSourceRepo.upsertChapter(chapter, manga.id)
        }.distinctUntilChanged().flowOn(Dispatchers.IO)
    }

    private fun canDownloadCover(mangaId: Int): Boolean {
        val cover = localSourceRepo.findCoverById(mangaId)
        val lastUpdate = cover?.lastModified() ?: 0L

        return System.currentTimeMillis() - lastUpdate > TimeUnit.DAYS.toMillis(1)
    }

    private fun fileExtensionFromUrl(url: String): String {
        return url.substringAfterLast(".", "obj")
    }

    companion object {
        const val PAGE_FORMAT = "%03d.%s"
    }
}