package com.vianh.blogtruyen.features.download

import android.content.Context
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.features.local.LocalSourceRepo
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.remote.MangaProvider
import com.vianh.blogtruyen.utils.await
import com.vianh.blogtruyen.utils.cancelableCatching
import com.vianh.blogtruyen.utils.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File


sealed class DownloadState {
    object Queued : DownloadState()
    data class InProgress(val progress: Int) : DownloadState()
    data class Error(val cause: Throwable) : DownloadState()
}

data class DownloadItem(
    val id: Int,
    val mangaId: Int,
    val coverImg: String,
    val name: String,
    val state: StateFlow<DownloadState>,
    val chapters: List<Chapter>
)


class DownloadHelper(
    private val context: Context,
    private val client: OkHttpClient,
    private val mangaProvider: MangaProvider,
    private val localSourceRepo: LocalSourceRepo
) {

    fun downloadManga(downloadItem: DownloadItem): Flow<Int> {
        return flow {
            val coverFileName = "${downloadItem.mangaId}.${fileExtensionFromUrl(downloadItem.coverImg)}"
            val cover = Glide.with(context).asFile().await(downloadItem.coverImg)

            cover?.copyTo(File(localSourceRepo.getCoverDir(), coverFileName))

            val chapters = downloadItem.chapters.mapNotNull {
                val pages = cancelableCatching {
                    mangaProvider.fetchChapterPage(it.url)
                }.getOrNull()

                if (pages != null)
                    it.copy(pages = pages)
                else
                    null
            }

            val totalPage = chapters.fold(0) { acc, chapter -> acc + chapter.pages.size }
            var successPage = 0

            chapters.forEach { chapter ->
                val chapterDir = localSourceRepo.getChapterDir(downloadItem.mangaId, downloadItem.name, chapter)

                chapter.pages.forEachIndexed { index, page ->
                    val request = Request.Builder().url(page).build()
                    val fileName = "$index.${fileExtensionFromUrl(page)}"

                    cancelableCatching {
                        val response = client.newCall(request).await()
                        val downloadedImage = File(chapterDir, fileName)
                        response.copyTo(downloadedImage)
                    }.onFailure { err ->
                        Timber.e(err)
                    }

                    successPage++
                    emit((successPage * 100f / totalPage).toInt())
                }
            }
        }.distinctUntilChanged().flowOn(Dispatchers.IO)
    }

    private fun fileExtensionFromUrl(url: String): String {
        return url.split(".").lastOrNull() ?: "obb"
    }
}