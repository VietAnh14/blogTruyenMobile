package com.vianh.blogtruyen

import com.vianh.blogtruyen.data.remote.BlogtruyenProvider
import com.vianh.blogtruyen.utils.BlogTruyenInterceptor
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test
import org.junit.Assert.*

class ParserUnitTest {

    private val client = OkHttpClient
        .Builder()
        .addInterceptor(BlogTruyenInterceptor())
        .build()
    private val blogtruyenProvider = BlogtruyenProvider(client)



    @Test
    fun testParseComments() {
        val mangaId = 25859
        val page = 1
        runBlocking {
            val comments = blogtruyenProvider.fetchComment(mangaId, page)
            print(comments)
            assertEquals(20, comments.size)
        }
    }
    
    @Test
    fun testParseNewFeed() {
        runBlocking {
            val response = blogtruyenProvider.fetchNewFeed()
        }
    }


    @Test
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
}