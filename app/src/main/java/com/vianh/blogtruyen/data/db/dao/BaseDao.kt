package com.vianh.blogtruyen.data.db.dao

import androidx.room.*


@Dao
abstract class BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(item: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(items: Collection<T>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(item: T): Int

    @Update
    abstract suspend fun update(items: List<T>)

    @Delete
    abstract suspend fun delete(item: T)

    @Transaction
    open suspend fun upsert(item: T): Boolean {
        if (update(item) <= 0) {
            insert(item)
            return false
        }
        return true
    }
}