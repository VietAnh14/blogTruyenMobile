package com.vianh.blogtruyen.data

import com.vianh.blogtruyen.data.local.AppDbHelper
import com.vianh.blogtruyen.data.local.DbHelper
import com.vianh.blogtruyen.data.remote.BlogtruyenProvider
import com.vianh.blogtruyen.data.remote.MangaProvider

object AppDataManager: DataManager {

    override fun getMangaProvider(): MangaProvider = BlogtruyenProvider
    override fun getDbHelper(): DbHelper = AppDbHelper
}