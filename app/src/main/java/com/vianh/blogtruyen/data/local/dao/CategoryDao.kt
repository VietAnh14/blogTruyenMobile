package com.vianh.blogtruyen.data.local.dao

import androidx.room.Dao
import androidx.room.Transaction
import com.vianh.blogtruyen.data.local.entity.CategoryEntity

@Dao
abstract class CategoryDao: BaseDao<CategoryEntity>() {
    @Transaction
    open suspend fun upsert(categories: List<CategoryEntity>) {
        categories.forEach {
            if (update(it) <= 0) {
                insert(it)
            }
        }
    }
}