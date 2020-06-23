package com.vianh.blogtruyen.data

import com.vianh.blogtruyen.data.remote.BlogtruyenProvider
import com.vianh.blogtruyen.data.remote.MangaProvider

class AppDataManager private constructor() : DataManager {

    override fun getMangaProvider(): MangaProvider = BlogtruyenProvider

    companion object Instance {
        @JvmStatic
        val INSTANCE: DataManager by lazy {
            AppDataManager()
        }
    }
}