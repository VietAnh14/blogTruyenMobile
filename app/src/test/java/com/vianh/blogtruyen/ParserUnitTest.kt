package com.vianh.blogtruyen

import com.vianh.blogtruyen.data.remote.BlogtruyenProvider
import com.vianh.blogtruyen.utils.ext.BlogTruyenInterceptor
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.*

class ParserUnitTest {

    private val client = OkHttpClient
        .Builder()
        .addInterceptor(BlogTruyenInterceptor())
        .build()
    private val blogtruyenProvider = BlogtruyenProvider(client)

    fun testParseComments() {
        val mangaId = 25859
        val page = 1
        runBlocking {
            val comments = blogtruyenProvider.getComment(mangaId, page)
            print(comments)
            assertEquals(20, comments.size)
        }
    }

    fun testParseNewFeed() {
        runBlocking {
            val response = blogtruyenProvider.getNewFeed()
        }
    }


    fun checkSize() {
        val request = Request.Builder().url("https://blogtruyen.vn").build()
        val mRequest = Request.Builder().url("https://m.blogtruyen.vn").build()
        val response = client.newCall(request).execute()
        val content = response.body?.contentLength() ?: 0

        val mResponse = client.newCall(mRequest).execute()
        val mContent = mResponse.body?.contentLength() ?: 0

        println("${content/1024} kB")
        println("${mContent/1024} kB")
    }


    fun testSearchHasFullPageResult() {
        runBlocking {
            val searchResults = blogtruyenProvider.searchByName("home", 1)
            assertEquals(20, searchResults.size)
        }
    }


    fun testSearchHasEmptyResult() {
        runBlocking {
            val searchResults = blogtruyenProvider.searchByName("some random search keywords", 1)
            assertEquals(0, searchResults.size)
        }
    }
}