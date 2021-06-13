package com.vianh.blogtruyen.features.history.data

import com.vianh.blogtruyen.data.local.MangaDb
import com.vianh.blogtruyen.data.local.entity.HistoryEntity
import com.vianh.blogtruyen.data.model.History
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map


interface HistoryRepository {
    fun observeHistory(): Flow<List<History>>

    suspend fun clearAllHistory()

    suspend fun deleteHistory(history: History)
}


class HistoryRepo(private val db: MangaDb): HistoryRepository {
    override fun observeHistory(): Flow<List<History>> {
        return db.historyDao
            .observeFullHistory()
            .map {
                it.map { fullHistory -> fullHistory.toHistory() }
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun clearAllHistory() {
        return db.historyDao.deleteAll()
    }

    override suspend fun deleteHistory(history: History) {
        return db.historyDao.delete(HistoryEntity.fromHistory(history))
    }
}