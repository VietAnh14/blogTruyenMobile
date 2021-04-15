package com.vianh.blogtruyen.data

import com.vianh.blogtruyen.data.local.DbHelper
import com.vianh.blogtruyen.data.remote.MangaProvider

interface DataManager {
    val dbHelper: DbHelper
    val mangaProvider: MangaProvider
}