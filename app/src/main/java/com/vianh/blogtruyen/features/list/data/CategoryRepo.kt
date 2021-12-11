package com.vianh.blogtruyen.features.list.data

import com.vianh.blogtruyen.data.db.MangaDb
import com.vianh.blogtruyen.data.model.Category
import com.vianh.blogtruyen.utils.mapList
import kotlinx.coroutines.flow.Flow

class CategoryRepo(private val db: MangaDb) {

    fun observeAll(): Flow<List<Category>> {
        return db.categoryDao.observeAll()
            .mapList { it.toCategory() }
    }
}