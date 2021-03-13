package com.vianh.blogtruyen.data

import com.vianh.blogtruyen.data.local.DbHelper
import com.vianh.blogtruyen.data.remote.MangaProvider

class AppDataManager(
    override val dbHelper: DbHelper,
    override val mangaProvider: MangaProvider) : DataManager