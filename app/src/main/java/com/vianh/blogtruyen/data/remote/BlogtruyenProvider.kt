package com.vianh.blogtruyen.data.remote

import com.vianh.blogtruyen.BuildConfig
import com.vianh.blogtruyen.data.model.Category
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.utils.ext.getBodyString
import com.vianh.blogtruyen.utils.ext.mapToSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BlogtruyenProvider(private val client: OkHttpClient) : MangaProvider {

    companion object {
        const val REFERER = BuildConfig.HOST
        private const val AJAX_LOAD_CHAPTER = BuildConfig.HOST + "/Chapter/LoadListChapter"
        private const val AJAX_LOAD_COMMENT = BuildConfig.HOST + "/Comment/AjaxLoadComment"
    }

    override val baseUrl: String = BuildConfig.HOST

    override suspend fun getMangaList(pageNumber: Int): List<Manga> {
        return withContext(Dispatchers.IO) {
            val url = BuildConfig.HOST_FULL + "/page-$pageNumber"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).getBodyString()
            parseListManga(Jsoup.parse(response))
        }
    }

    override suspend fun getMangaDetails(manga: Manga): Manga {
        return withContext(Dispatchers.IO) {
            val url = BuildConfig.HOST + manga.link
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).getBodyString()
            parseDetailManga(response, manga)
        }
    }

    override suspend fun getChapterList(mangaId: Int): List<Chapter> {
        return withContext(Dispatchers.IO) {
            val result: MutableList<Chapter> = mutableListOf()
            var lastPage: Int
            var currentPage = 1
            do {
                val url = "$AJAX_LOAD_CHAPTER?id=${mangaId}&p=${currentPage}"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).getBodyString()
                val docs = Jsoup.parse(response)
                val options = docs.getElementsByClass("slcChangePage")
                lastPage = if (!options.isEmpty()) {
                    options[0].getElementsByTag("option")
                        .last()
                        ?.attr("value")?.toInt() ?: 1
                } else {
                    1
                }

                val items = docs.getElementById("listChapter")
                    ?.children() ?: throwParseFail("Failed to parse chapter list")
                result.addAll(parseSingleListChapter(items))
                currentPage++
            } while (lastPage >= currentPage)
            val size = result.size

            return@withContext result.mapIndexed { index, chapter ->
                chapter.copy(number = size - index)
            }
        }
    }

    override suspend fun getChapterPage(link: String): List<String> {
        return withContext(Dispatchers.IO) {
            val url = BuildConfig.HOST_FULL + link
            val request = Request.Builder().url(url).build()
            val content = client.newCall(request).getBodyString()
            return@withContext parseChapter(content)
        }
    }

    override suspend fun getComment(mangaId: Int, offset: Int): Map<Comment, List<Comment>> {
        return withContext(Dispatchers.IO) {
            val uri = "$AJAX_LOAD_COMMENT?mangaId=$mangaId&p=$offset"
            val request = Request.Builder().url(uri).build()
            val content = client.newCall(request).getBodyString()
            parseComment(content)
        }
    }

    override suspend fun getNewFeed(): FeedItem {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(BuildConfig.HOST_FULL).build()
            val response = client.newCall(request).getBodyString()
            val doc = Jsoup.parse(response)
            val pinStories = getPinStories(doc)
            val newUpdates = parseListManga(doc)
            val newManga = parseNewUpdate(doc)

            FeedItem(pinStories, newUpdates, newManga)
        }
    }

    override suspend fun searchByName(query: String, pageNumber: Int): List<Manga> {
        return withContext(Dispatchers.IO) {
            val url = "${BuildConfig.HOST_FULL}/timkiem/nangcao/1/0/-1/-1?txt=$query&p=$pageNumber"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).getBodyString()
            val doc = Jsoup.parse(response)
            parseSearchDetails(doc)
        }
    }

    private fun parseSearchDetails(doc: Document): List<Manga> {
        val searchContent = doc
            .getElementsByClass("list-manga-bycate").getOrNull(0) ?: return emptyList()

        val searchResultList = searchContent.removeClass("upperCase")
        val infos = searchResultList.select("p:not([class])")
        val hiddenTips = searchResultList.getElementsByClass("hidden tiptip-content")

        val mangas = infos.mapIndexed { index, element ->
            val a = element.getElementsByTag("a")[0]
            val tip = hiddenTips.getOrNull(index)

            val link = a.attr("href")
            val title = a.text()
            val id = idFromRelativeLink(link)
            val imageUrl = tip?.getElementsByTag("img")?.attr("src")
            val rawDes = tip?.getElementsByClass("al-j fs-12")?.getOrNull(0)?.text()
            val des = if (rawDes.isNullOrBlank()) "Updating" else rawDes

            Manga(
                id = id,
                imageUrl = imageUrl.orEmpty(),
                title = title,
                uploadTitle = title,
                description = des,
                link = link
            )
        }

        return mangas
    }

    private fun parseNewUpdate(doc: Document): List<Manga> {
        val newStories = doc.getElementById("top-newest-story") ?: return emptyList()
        return newStories.getElementsByTag("a").map {
            val link = it.attr("href")
            val title = it.attr("title")
            val imageUrl = it.child(0).attr("src")
            Manga(
                id = idFromRelativeLink(link),
                title = title,
                link = link,
                imageUrl = imageUrl,
                uploadTitle = title,
                description = "Updating"
            )
        }
    }

    private fun parseListManga(doc: Document): List<Manga> {
        val list = doc.getElementsByClass("list-mainpage")[0].child(0)
        val listItems = list.getElementsByClass("storyitem")
        return listItems.map {
            val linkTag = it.child(0).child(0)
            val link = linkTag.attr("href")
            val id = idFromRelativeLink(link)
            val title = linkTag.attr("title")
            val imageLink = linkTag.getElementsByTag("img")
                .last()!!
                .attr("src")

            val des = it.child(1).child(1).text()
            val categoryNodes = it
                .getElementsByClass("category")[0]
                .getElementsByTag("a")

            val categories = categoryNodes.mapToSet { category ->
                Category(category.text(), category.attr("href"))
            }

            Manga(
                id = id,
                imageUrl = imageLink,
                title = title,
                uploadTitle = "",
                description = des,
                link = link,
                categories = categories
            )
        }
    }

    private fun getPinStories(doc: Document): List<Manga> {
        val pinedArticles = doc.getElementById("storyPinked")
            ?.getElementsByTag("article")?.getOrNull(0) ?: return emptyList()
        val mangaLinks = pinedArticles.getElementsByClass("tiptip")
        return mangaLinks.map {
            val link = it.attr("href")
            val id = link.split("/")[1].toInt()
            val imageUrl = it.child(0).attr("src")
            val tip = pinedArticles.getElementById("tiptip-$id") ?: throwParseFail("Failed to parse")
            val title = tip.child(0).text()
            val des = tip.child(1).text().ifEmpty { "Updating" }

            Manga(
                id = id,
                imageUrl = imageUrl,
                title = title,
                uploadTitle = "",
                description = des,
                link = link
            )
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
            val userName = commentContent?.getElementsByClass("user")
                ?.first()
                ?.firstElementSibling()
                ?.text().orEmpty()
            val time = commentContent?.getElementsByClass("time")
                ?.first()
                ?.text().orEmpty()
            val message = commentContent?.getElementsByClass("commment-content")
                ?.first()
                ?.text().orEmpty()

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
                .first()
                ?.attr("src")
                .orEmpty()
            val userName = subComment.getElementsByClass("user")
                .first()
                ?.text()
                .orEmpty()
            val time = subComment.getElementsByClass("time")
                .first()
                ?.text().orEmpty()
            val message = subComment.getElementsByClass("commment-content")
                .first()
                ?.text().orEmpty()

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
            val updateDateString = row.child(1).text()
            val updateDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .parse(updateDateString)?.time ?: 0

            val id = link.split('/')[1]

            result.add(
                Chapter(
                    id = id,
                    name = title,
                    url = link,
                    number = 0,
                    uploadDate = updateDate
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
        val id = details.getElementById("MangaId")
            ?.attr("value")?.toInt() ?: throwParseFail("Fail to parse manga")
        val description = details.getElementsByClass("introduce")[0]
            .text().ifBlank { "Updating" }
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
        val items = Jsoup.parse(html)
            .getElementsByClass("list-manga")
            .firstOrNull()
            ?.getElementsByClass("item")
            .orEmpty()

        val listManga = mutableListOf<Manga>()
        for (item in items) {
            val title = item.getElementsByClass("title").firstOrNull()?.text().orEmpty()
            val image = item.getElementsByTag("img").firstOrNull()?.attr("src")
            val link = item.getElementsByTag("a").firstOrNull()?.attr("href").orEmpty()
            val id = idFromRelativeLink(link)
            val des = item.getElementsByClass("introduce-home").firstOrNull()?.text()
            val manga = Manga(
                imageUrl = image.orEmpty(),
                link = link,
                uploadTitle = title,
                title = title,
                id = id,
                description = if (des.isNullOrEmpty()) "Updating" else des
            )
            listManga.add(manga)
        }
        return listManga
    }

    private fun parseChapter(html: String): List<String> {
        val images = mutableListOf<String>()
        val doc = Jsoup.parse(html)
        val content = doc.getElementById("content")

        val contentImages = requireNotNull(content).children()
            .filter { it.tagName() == "img" }
            .map { it.attr("src") }
        images.addAll(contentImages)

        // Some chapters are render by js script
        val script = content.getElementsByTag("script").lastOrNull()
        if (script != null && script.data().contains("listImageCaption")) {
            val listImageCaption = script.data().split(";")[0].split("=")[1].trim()
            val imageArr = JSONArray(listImageCaption)
            for (i in 0 until imageArr.length()) {
                images.add(imageArr.getJSONObject(i).getString("url"))
            }
        }

        return images
    }

    private fun idFromRelativeLink(link: String): Int {
        return link.split('/')[1].toInt()
    }

    private fun throwParseFail(message: String): Nothing {
        throw IllegalArgumentException(message)
    }
}