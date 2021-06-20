package com.vianh.blogtruyen.features.favorites.data

import androidx.room.Dao
import androidx.room.Query
import com.vianh.blogtruyen.data.local.dao.BaseDao
import com.vianh.blogtruyen.data.local.entity.FavoriteEntity
import com.vianh.blogtruyen.data.local.entity.FavoriteWithManga
import com.vianh.blogtruyen.data.local.entity.FullHistory
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavoriteDao: BaseDao<FavoriteEntity>() {

    @Query("Delete from favorites where sourceMangaId = :mangaId")
    abstract suspend fun delete(mangaId: Int)

    @Query("Select * from favorites")
    abstract fun observeAll(): Flow<FavoriteWithManga>

    @Query("Select * from favorites where sourceMangaId = :mangaId")
    abstract fun observeByMangaId(mangaId: Int): Flow<FavoriteEntity?>
}