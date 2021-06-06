package com.vianh.blogtruyen.data.local.dao

import androidx.room.Dao
import com.vianh.blogtruyen.data.local.entity.FullHistory
import com.vianh.blogtruyen.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class HistoryDao: BaseDao<HistoryEntity>() {
    abstract fun observeFullHistory(): Flow<FullHistory>
}