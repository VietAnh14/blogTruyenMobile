package com.vianh.blogtruyen.features.favorites.data

import com.vianh.blogtruyen.data.local.MangaDb
import com.vianh.blogtruyen.data.local.entity.FavoriteEntity
import com.vianh.blogtruyen.data.model.Favorite
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.utils.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map


interface FavoriteRepository {
    suspend fun addToFavorite(manga: Manga)

    suspend fun removeFromFavorite(mangaId: Int)

    suspend fun upsertFavorite(favorite: Favorite)

    suspend fun clearNewChapters(mangaId: Int)

    fun observeFavorite(): Flow<List<Favorite>>

    fun observeFavoriteState(mangaId: Int): Flow<Favorite?>
}

class FavoriteRepo(private val db: MangaDb) : FavoriteRepository {
    override suspend fun addToFavorite(manga: Manga) {
        db.favoriteDao.upsert(FavoriteEntity.fromManga(manga))
    }

    override suspend fun removeFromFavorite(mangaId: Int) {
        db.favoriteDao.delete(mangaId)
    }

    override suspend fun upsertFavorite(favorite: Favorite) {
        db.favoriteDao.upsert(FavoriteEntity.fromFavorite(favorite))
    }

    override suspend fun clearNewChapters(mangaId: Int) {
        val favorite = db.favoriteDao.observeByMangaId(mangaId).firstOrNull()?.favorite
        if (favorite != null) {
            val currentChapterCount = favorite.currentChapterCount + favorite.newChapterCount
            val freshFavorite = favorite
                .copy(newChapterCount = 0, currentChapterCount = currentChapterCount)
            db.favoriteDao.update(freshFavorite)
        }
    }

    override fun observeFavorite(): Flow<List<Favorite>> {
        return db.favoriteDao
            .observeAll()
            .mapList { it.toFavorite() }
            .flowOn(Dispatchers.IO)
    }

    override fun observeFavoriteState(mangaId: Int): Flow<Favorite?> {
        return db.favoriteDao
            .observeByMangaId(mangaId)
            .map { it?.toFavorite() }
            .flowOn(Dispatchers.IO)
    }
}