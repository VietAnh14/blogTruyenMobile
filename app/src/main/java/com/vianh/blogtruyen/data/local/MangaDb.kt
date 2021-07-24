package com.vianh.blogtruyen.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vianh.blogtruyen.data.local.dao.CategoryDao
import com.vianh.blogtruyen.data.local.dao.ChapterDao
import com.vianh.blogtruyen.features.history.data.HistoryDao
import com.vianh.blogtruyen.features.mangaDetails.data.MangaDao
import com.vianh.blogtruyen.data.local.entity.*
import com.vianh.blogtruyen.features.favorites.data.FavoriteDao
import kotlinx.coroutines.*

@Database(
    entities = [
        MangaEntity::class,
        ChapterEntity::class,
        MangaCategory::class,
        CategoryEntity::class,
        HistoryEntity::class,
        FavoriteEntity::class
    ],
    exportSchema = false,
    version = 1
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
            var mangaDb: MangaDb? = null
//            val callback = object : RoomDatabase.Callback() {
//                override fun onCreate(db: SupportSQLiteDatabase) {
//                    super.onCreate(db)
//                    mangaDb?.let {
//                        GlobalScope.launch(Dispatchers.IO) {
//                            it.categoryDao().insert(categories)
//                        }
//                    }
//                }
//            }

            mangaDb = Room.databaseBuilder(context, MangaDb::class.java, DB_NAME)
//                .addCallback(callback)
                .build()
            return mangaDb
        }
    }
}