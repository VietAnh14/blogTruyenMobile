package com.vianh.blogtruyen.features.favorites.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.vianh.blogtruyen.data.db.dao.BaseDao
import com.vianh.blogtruyen.data.db.entity.FavoriteEntity
import com.vianh.blogtruyen.data.db.entity.FavoriteWithManga
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavoriteDao: BaseDao<FavoriteEntity>() {

    @Query("Delete from favorites where sourceMangaId = :mangaId")
    abstract suspend fun delete(mangaId: Int)

    @Transaction
    @Query("Select * from favorites order by createdTime desc")
    abstract fun observeAll(): Flow<List<FavoriteWithManga>>

    @Transaction
    @Query("Select * from favorites where sourceMangaId = :mangaId")
    abstract fun observeByMangaId(mangaId: Int): Flow<FavoriteWithManga?>

    @Query("Select * from favorites where sourceMangaId = :mangaId")
    abstract suspend fun findByMangaId(mangaId: Int): FavoriteEntity?

    @Query("Select sum(newChapterCount) from favorites")
    abstract fun getTotalNotification(): Flow<Int?>
}