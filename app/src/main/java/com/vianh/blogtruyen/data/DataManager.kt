package com.vianh.blogtruyen.data

import com.vianh.blogtruyen.data.local.DbHelper
import com.vianh.blogtruyen.data.remote.MangaProvider

interface DataManager {
    fun getMangaProvider(): MangaProvider
    fun getDbHelper(): DbHelper
}