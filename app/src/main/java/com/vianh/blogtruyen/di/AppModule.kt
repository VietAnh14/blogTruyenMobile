package com.vianh.blogtruyen.di

import android.content.Context
import com.vianh.blogtruyen.data.AppDataManager
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.local.AppDbHelper
import com.vianh.blogtruyen.data.local.DbHelper
import com.vianh.blogtruyen.features.local.LocalSourceRepo
import com.vianh.blogtruyen.data.local.MangaDb
import com.vianh.blogtruyen.data.remote.BlogtruyenProvider
import com.vianh.blogtruyen.data.remote.MangaProvider
import com.vianh.blogtruyen.utils.BlogTruyenInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.dsl.module
import java.io.File
import java.util.concurrent.TimeUnit

val appModule
    get() = module {
        single { MangaDb.provideDb(get()) }
        single { provideClient(get()) }
        single<DbHelper> { AppDbHelper(get()) }
        single<MangaProvider> { BlogtruyenProvider(get()) }
        single<DataManager> { AppDataManager(get(), get()) }

        single { LocalSourceRepo(get(), get()) }
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