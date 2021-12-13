package com.vianh.blogtruyen.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.vianh.blogtruyen.data.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT * FROM categories")
    abstract fun observeAll(): Flow<List<CategoryEntity>>
}