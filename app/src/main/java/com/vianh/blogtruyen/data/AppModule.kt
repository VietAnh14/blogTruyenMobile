package com.vianh.blogtruyen.data

import android.content.Context
import com.vianh.blogtruyen.data.github.GithubRepo
import com.vianh.blogtruyen.data.db.AppDbHelper
import com.vianh.blogtruyen.data.db.DbHelper
import com.vianh.blogtruyen.data.db.MangaDb
import com.vianh.blogtruyen.data.remote.BlogtruyenProvider
import com.vianh.blogtruyen.data.remote.MangaProvider
import com.vianh.blogtruyen.utils.ext.BlogTruyenInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.dsl.module
import java.io.File
import java.util.concurrent.TimeUnit

val dataModule
    get() = module {
        single { MangaDb.provideDb(get()) }
        single { provideClient(get()) }
        single { GithubRepo(get()) }
        single<DbHelper> { AppDbHelper(get()) }
        single<MangaProvider> { BlogtruyenProvider(get()) }
    }

private fun provideClient(context: Context): OkHttpClient {
    return OkHttpClient
        .Builder()
        .cache(Cache(File(context.cacheDir, "http_cache"), 40 * 1024 * 1024L))
        .addInterceptor(BlogTruyenInterceptor())
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}