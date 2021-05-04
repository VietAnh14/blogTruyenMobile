package com.vianh.blogtruyen.data.local.dao

import androidx.room.*


@Dao
abstract class BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(item: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(items: List<T>): List<Long>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun update(item: T)

    @Update
    abstract suspend fun update(items: List<T>)

    @Delete
    abstract suspend fun delete(item: T)

    @Transaction
    open suspend fun upsert(item: T) {
        val id = insert(item)
        if (id == -1L) {
            update(item)
        }
    }

    @Transaction
    open suspend fun upsert(objList: List<T>) {
        val insertResult = insert(objList)
        val updateList: MutableList<T> = ArrayList()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) {
                updateList.add(objList[i])
            }
        }
        if (updateList.isNotEmpty()) {
            update(updateList)
        }
    }
}