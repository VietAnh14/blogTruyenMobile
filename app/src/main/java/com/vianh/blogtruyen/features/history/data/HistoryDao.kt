package com.vianh.blogtruyen.features.history.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.vianh.blogtruyen.data.local.dao.BaseDao
import com.vianh.blogtruyen.data.local.entity.FullHistory
import com.vianh.blogtruyen.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class HistoryDao: BaseDao<HistoryEntity>() {

    @Transaction
    @Query("Select * from history order by lastRead desc")
    abstract fun observeFullHistory(): Flow<List<FullHistory>>

    @Query("Delete from history")
    abstract suspend fun deleteAll()
}