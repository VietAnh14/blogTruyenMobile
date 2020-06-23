package com.vianh.blogtruyen.data.remote

import com.vianh.blogtruyen.BuildConfig
import com.vianh.blogtruyen.MvvmApp
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.utils.extractData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.File

object BlogtruyenProvider : MangaProvider {

    val client by lazy {
        OkHttpClient.Builder().cache(
            // 5 Mb cache
            Cache(File(MvvmApp.app.cacheDir, "http_cache"), 40 * 1024 * 1024)
        ).build()
    }
    const val DASHBOARD = BuildConfig.HOST + "/thumb"
    const val AJAX_LOAD_CHAPTER = BuildConfig.HOST + "/Chapter/LoadListChapter"

    override suspend fun fetchNewManga(): List<Manga> {
        val request = Request.Builder().url(DASHBOARD).build()
        val listManga = withContext(Dispatchers.IO) {
            val response = client.newCall(request).extractData()
            parseManga(response)
        }
        return listManga
    }

    override suspend fun fetchDetailManga(manga: Manga): Manga{
        val url = BuildConfig.HOST + manga.link
        val request = Request.Builder().url(url).build()
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).extractData()
            parseDetailManga(response, manga)
        }
    }

    override suspend fun fetchChapterList(manga: Manga): List<Chapter> {
        val result: MutableList<Chapter> = mutableListOf()

        withContext(Dispatchers.IO) {
            var lastPage: Int
            var currentPage = 1
            do {
                val url = "$AJAX_LOAD_CHAPTER?id=${manga.id}&p=${currentPage}"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).extractData()
                val docs = Jsoup.parse(response)
                val options = docs.getElementsByClass("slcChangePage")
                lastPage = if (!options.isEmpty()) {
                    options[0].getElementsByTag("option").last().attr("value").toInt()
                } else{
                    1
                }
                val items = docs.getElementById("listChapter").children()
                result.addAll(parseSingleListChapter(items))
                currentPage++
            } while (lastPage >= currentPage)
        }
        return result
    }

    private fun parseSingleListChapter(items: Elements): List<Chapter> {
        val result: MutableList<Chapter> = mutableListOf()
        for (element in items) {
            val row = element.child(0)
            val title = row.child(0).child(0).text()
            val link = row.child(0).child(0).attr("href")
            val uploadDate = row.child(1).text()
            result.add(Chapter(link, title, uploadDate))
        }
        return result
    }

    fun parseDetailManga(html: String, manga: Manga): Manga {
        val doc = Jsoup.parse(html)
        val details = doc.getElementsByClass("manga-detail")[0]
        val title = details.getElementsByClass("title")[0].text()
        val image = details.getElementsByClass("content")[0].child(0).attr("src")
        val id = details.getElementById("MangaId").attr("value").toInt()
        val description = details.getElementsByClass("introduce")[0].text()
        return Manga(image, manga.link, title, manga.uploadTitle, description, id)
    }

    fun parseManga(html: String): List<Manga> {
        val items = Jsoup.parse(html).getElementsByClass("ps-relative")
        val listManga = mutableListOf<Manga>()
        for (item in items) {
            val title = item.child(1).child(0)
            val image = item.child(0).child(0).attr("src")
            val manga = Manga(
                image,
                title.attr("href"),
                uploadTitle = title.text(),
                title = title.text()
            )
            listManga.add(manga)
        }
        return listManga
    }
}