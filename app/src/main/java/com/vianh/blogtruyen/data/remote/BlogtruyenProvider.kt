package com.vianh.blogtruyen.data.remote

import com.vianh.blogtruyen.MvvmApp
import com.vianh.blogtruyen.data.PreviewManga
import com.vianh.blogtruyen.utils.extractData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.File

class BlogtruyenProvider private constructor() : MangaProvider {
    companion object HOLDER {
        @JvmStatic
        val instance by lazy { BlogtruyenProvider() }
        val client by lazy {
            OkHttpClient.Builder().cache(
                Cache(File(MvvmApp.app.cacheDir, "http_cache"), 50 * 1024 * 1024)
            ).build()
        }
        const val BASE_URL = "https://m.blogtruyen.vn/thumb"
    }

    override suspend fun fetchNewManga(): List<PreviewManga> {
        val request = Request.Builder().url(BASE_URL).build()
        val listManga = withContext(Dispatchers.IO) {
            val response = client.newCall(request).extractData()
            parseManga(response)
        }
        return listManga
    }

    fun parseManga(html: String): List<PreviewManga> {
        val items = Jsoup.parse(html).getElementsByClass("ps-relative")
        val listManga = mutableListOf<PreviewManga>()
        for (item in items) {
            val title = item.child(1).child(0)
            val image = item.child(0).child(0).attr("src")
            val manga = PreviewManga(image, title.attr("href"), title.text())
            listManga.add(manga)
        }
        return listManga
    }
}