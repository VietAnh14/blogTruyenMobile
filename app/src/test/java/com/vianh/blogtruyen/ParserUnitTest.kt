package com.vianh.blogtruyen

import com.vianh.blogtruyen.data.remote.BlogtruyenProvider
import com.vianh.blogtruyen.utils.BlogTruyenInterceptor
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test
import org.junit.Assert.*

class ParserUnitTest {

    val client = OkHttpClient
        .Builder()
        .addInterceptor(BlogTruyenInterceptor())
        .build()
    val blogtruyenProvider = BlogtruyenProvider(client)



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
}