package com.vianh.blogtruyen.data.remote

import com.github.michaelbull.result.runCatching
import com.vianh.blogtruyen.BuildConfig
import com.vianh.blogtruyen.data.MonadResult
import com.vianh.blogtruyen.data.model.Category
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.utils.extractData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.util.*
import kotlin.collections.ArrayList

class BlogtruyenProvider(private val client: OkHttpClient) : MangaProvider {

    companion object {
        private const val AJAX_LOAD_CHAPTER = BuildConfig.HOST + "/Chapter/LoadListChapter"
        private const val AJAX_LOAD_COMMENT = BuildConfig.HOST + "/Comment/AjaxLoadComment"
    }

    override suspend fun fetchNewManga(pageNumber: Int): MonadResult<MutableList<Manga>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val url = BuildConfig.HOST + "/thumb-$pageNumber"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).extractData()
                parseManga(response)
            }
        }
    }

    override suspend fun fetchDetailManga(manga: Manga): Manga {
        return withContext(Dispatchers.IO) {
            val url = BuildConfig.HOST + manga.link
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).extractData()
            parseDetailManga(response, manga)
        }
    }

    override suspend fun fetchChapterList(mangaId: Int): List<Chapter> {
        return withContext(Dispatchers.IO) {
            val result: MutableList<Chapter> = mutableListOf()
            var lastPage: Int
            var currentPage = 1
            do {
                val url = "$AJAX_LOAD_CHAPTER?id=${mangaId}&p=${currentPage}"
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
                result.addAll(parseSingleListChapter(items))
                currentPage++
            } while (lastPage >= currentPage)
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

    override suspend fun fetchComment(mangaId: Int, offset: Int): Map<Comment, List<Comment>> {
        return withContext(Dispatchers.IO) {
            val uri = "$AJAX_LOAD_COMMENT?mangaId=$mangaId&p=$offset"
            val request = Request.Builder().url(uri).build()
            val content = client.newCall(request).extractData()
            parseComment(content)
        }
    }

    private fun parseComment(html: String): Map<Comment, List<Comment>> {
        val doc = Jsoup.parse(html)
        val commentSessions = doc.getElementsByClass("ul-comment")
        val result = LinkedHashMap<Comment, List<Comment>>()
        for (session in commentSessions) {
            val avatar = session
                .child(0)
                .child(0)
                .getElementsByClass("img-avatar")
                .attr("src")
            val commentContent = session
                .getElementsByClass("c-content")
                .first()
            val userName = commentContent.getElementsByClass("user")
                .first()
                .firstElementSibling()
                .text()
            val time = commentContent.getElementsByClass("time")
                .first()
                .text()
            val message = commentContent.getElementsByClass("commment-content")
                .first()
                .text()
            val subCommentsSession = session.getElementsByClass("sub-c-item")
            val subComments = parseSubComment(subCommentsSession)
            val rootComment = Comment(
                userName = userName,
                avatar = avatar,
                message = message,
                time = time,
                type = Comment.TOP
            )
            result[rootComment] = subComments
        }
        return result
    }

    private fun parseSubComment(elements: Elements): List<Comment> {
        val comments = ArrayList<Comment>()
        for (subComment in elements) {
            val avatar = subComment.getElementsByClass("img-avatar")
                .first().attr("src")
            val userName = subComment.getElementsByClass("user")
                .first()
                .text()
            val time = subComment.getElementsByClass("time")
                .first()
                .text()
            val message = subComment.getElementsByClass("commment-content")
                .first()
                .text()

            val comment = Comment(
                userName = userName,
                avatar = avatar,
                message = message,
                time = time,
                type = Comment.REPLY
            )
            comments.add(comment)
        }
        return comments
    }

    private fun parseSingleListChapter(items: Elements): List<Chapter> {
        val result: MutableList<Chapter> = mutableListOf()
        for (element in items) {
            val row = element.child(0)
            val title = row.child(0).child(0).text()
            val link = row.child(0).child(0).attr("href")
            val id = link.split('/')[1]
            result.add(
                Chapter(
                    id = id,
                    name = title,
                    url = link
                )
            )
        }
        return result
    }

    private fun parseDetailManga(html: String, manga: Manga): Manga {
        val doc = Jsoup.parse(html)
        val details = doc.getElementsByClass("manga-detail")[0]
        val title = details.getElementsByClass("title")[0].text()
        val image = details.getElementsByClass("content")[0].child(0).attr("src")
        val id = details.getElementById("MangaId").attr("value").toInt()
        val description = details.getElementsByClass("introduce")[0].text()
        val category = details.getElementsByClass("catetgory")[0]
            .child(0)
            .children()
            .filter { !it.hasClass("first") }
            .map {
                Category(
                    name = it.text(),
                    query = it.child(0).attr("href")
                )
            }
            .toSet()
        return manga.copy(
            id = id,
            title = title,
            description = description,
            imageUrl = image,
            categories = category
        )
    }

    private fun parseManga(html: String): MutableList<Manga> {
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
                id = id,
                description = "Updating"
            )
            listManga.add(manga)
        }
        return listManga
    }

    private fun parseChapter(html: String): List<String> {
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