package com.vianh.blogtruyen.data.github

import com.vianh.blogtruyen.utils.ext.getBodyString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class GithubRepo(private val client: OkHttpClient) {

    companion object {
        const val RELEASE_ENDPOINT = "https://api.github.com/repos/VietAnh14/blogtruyenMobile/releases/latest"
        const val ACCEPT_HEADER = "application/vnd.github.v3+json"
    }

    suspend fun getLatestRelease(): Release {
        return withContext(Dispatchers.IO) {
            val endpoint = RELEASE_ENDPOINT
                .toHttpUrl()

            val request = Request.Builder()
                .url(endpoint)
                .header("Accept", ACCEPT_HEADER)
                .get().build()

            val response = client.newCall(request).getBodyString()
            val releaseJson = JSONObject(response)
            val name = releaseJson.getString("name")
            val url = releaseJson.getString("html_url")
            val assets = releaseJson
                .getJSONArray("assets")
                .getJSONObject(0)

            val size = assets.getLong("size")
            val downloadUrl = assets.getString("browser_download_url")

            val createdDateString = releaseJson.getString("created_at")
            val createdDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                .parse(createdDateString)

            return@withContext Release(url, downloadUrl, name, size, createdDate)
        }
    }
}