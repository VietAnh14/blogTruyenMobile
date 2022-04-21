package com.vianh.blogtruyen.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vianh.blogtruyen.data.db.dao.CategoryDao
import com.vianh.blogtruyen.data.db.dao.ChapterDao
import com.vianh.blogtruyen.features.history.data.HistoryDao
import com.vianh.blogtruyen.features.details.data.MangaDao
import com.vianh.blogtruyen.data.db.entity.*
import com.vianh.blogtruyen.features.favorites.data.FavoriteDao

@Database(
    entities = [
        MangaEntity::class,
        ChapterEntity::class,
        MangaCategory::class,
        CategoryEntity::class,
        HistoryEntity::class,
        FavoriteEntity::class
    ],
    exportSchema = true,
    version = 2
)
abstract class MangaDb : RoomDatabase() {
    abstract val mangaDao: MangaDao
    abstract val categoryDao: CategoryDao
    abstract val chapterDao: ChapterDao
    abstract val historyDao: HistoryDao
    abstract val favoriteDao: FavoriteDao

    companion object {
        private const val DB_NAME = "MangaDB.db"

        fun provideDb(context: Context): MangaDb {
            return Room.databaseBuilder(context, MangaDb::class.java, DB_NAME)
                .addMigrations(Migrations.MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}