package com.vianh.blogtruyen.data.remote

import android.util.Log
import com.vianh.blogtruyen.BuildConfig
import com.vianh.blogtruyen.MvvmApp
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.utils.BlogTruyenInterceptor
import com.vianh.blogtruyen.utils.extractData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.File
import java.lang.ArithmeticException

object BlogtruyenProvider : MangaProvider {

    val client by lazy {
        OkHttpClient.Builder().cache(
            // 5 Mb cache
            Cache(File(MvvmApp.app.cacheDir, "http_cache"), 40 * 1024 * 1024)
        ).addInterceptor(BlogTruyenInterceptor()).build()
    }
    const val AJAX_LOAD_CHAPTER = BuildConfig.HOST + "/Chapter/LoadListChapter"

    override suspend fun fetchNewManga(pageNumber: Int): MutableList<Manga> {
        val url = BuildConfig.HOST + "/thumb-$pageNumber"
        val request = Request.Builder().url(url).build()
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).extractData()
            parseManga(response)
        }
    }

    override suspend fun fetchDetailManga(manga: Manga): Manga {
        val url = BuildConfig.HOST + manga.link
        val request = Request.Builder().url(url).build()
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).extractData()
            parseDetailManga(response, manga)
        }
    }

    override suspend fun fetchChapterList(manga: Manga): List<Chapter> {
        return withContext(Dispatchers.IO) {
            val result: MutableList<Chapter> = mutableListOf()
            var lastPage: Int
            var currentPage = 1
            do {
                val url = "$AJAX_LOAD_CHAPTER?id=${manga.mangaId}&p=${currentPage}"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).extractData()
                val docs = Jsoup.parse(response)
                val options = docs.getElementsByClass("slcChangePage")
                lastPage = if (!options.isEmpty()) {
                    options[0].getElementsByTag("option").last().attr("value").toInt()
                } else {
                    1
                }
                val items = docs.getElementById("listChapter").children()
                result.addAll(parseSingleListChapter(items, manga.mangaId))
                currentPage++
            } while (lastPage >= currentPage)
            Log.d("Fetch chapter list done", System.currentTimeMillis().toString())
            return@withContext result
        }
    }

    override suspend fun fetchChapterPage(link: String): List<String> {
        return withContext(Dispatchers.IO) {
            val url = BuildConfig.HOST_FULL + link
            val request = Request.Builder().url(url).build()
            val content = client.newCall(request).extractData()
            return@withContext parseChapter(content)
        }
    }

    private fun parseSingleListChapter(items: Elements, mangaId: Int): List<Chapter> {
        val result: MutableList<Chapter> = mutableListOf()
        for (element in items) {
            val row = element.child(0)
            val title = row.child(0).child(0).text()
            val link = row.child(0).child(0).attr("href")
            val id = link.split('/')[1]
            result.add(Chapter(link, title, id, mangaId))
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
        Log.d("Fetch detail done", System.currentTimeMillis().toString())
        return Manga(image, manga.link, title, manga.uploadTitle, description, id)
    }

    fun parseManga(html: String): MutableList<Manga> {
        val items = Jsoup.parse(html).getElementsByClass("ps-relative")
        val listManga = mutableListOf<Manga>()
        for (item in items) {
            val title = item.child(1).child(0)
            val image = item.child(0).child(0).attr("src")
            val link = title.attr("href")
            val id = link.split('/')[1].toInt()
            val manga = Manga(
                imageUrl = image,
                link = link,
                uploadTitle = title.text(),
                title = title.text(),
                mangaId = id
            )
            listManga.add(manga)
        }
        return listManga
    }

    fun parseChapter(html: String): List<String> {
        val images = mutableListOf<String>()
        val doc = Jsoup.parse(html)
        val content = doc.getElementById("content")

        // Check if chapter is render by angular
        val item = content.child(0)
        if (item.tagName() == "img") {
            val elements = content.getElementsByTag("img")
            for (image in elements) {
                images.add(image.attr("src"))
            }
        } else {
            val script = content.children().last()?.data()
            script?.let {
                val listImageCaption = it.split(";")[0].trim().split(" ")[3]
                val imageArr = JSONArray(listImageCaption)
                for (i in 0 until imageArr.length()) {
                    images.add(imageArr.getJSONObject(i).getString("url"))
                }
            }
        }
        return images
    }
}