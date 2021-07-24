package com.vianh.blogtruyen.data

import com.vianh.blogtruyen.data.local.DbHelper
import com.vianh.blogtruyen.data.remote.MangaProvider

@Deprecated("Create repository instead")
interface DataManager {
    val dbHelper: DbHelper
    val mangaProvider: MangaProvider
}