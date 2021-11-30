package com.vianh.blogtruyen.features.history.data

import com.vianh.blogtruyen.data.db.MangaDb
import com.vianh.blogtruyen.data.db.entity.HistoryEntity
import com.vianh.blogtruyen.data.model.History
import com.vianh.blogtruyen.utils.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn


interface HistoryRepository {
    fun observeHistory(): Flow<List<History>>

    suspend fun clearAllHistory()

    suspend fun deleteHistory(history: History)
}


class HistoryRepo(private val db: MangaDb): HistoryRepository {
    override fun observeHistory(): Flow<List<History>> {
        return db.historyDao
            .observeFullHistory()
            .mapList { it.toHistory() }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun clearAllHistory() {
        return db.historyDao.deleteAll()
    }

    override suspend fun deleteHistory(history: History) {
        return db.historyDao.delete(HistoryEntity.fromHistory(history))
    }
}