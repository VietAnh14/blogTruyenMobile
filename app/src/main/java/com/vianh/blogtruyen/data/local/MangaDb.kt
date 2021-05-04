package com.vianh.blogtruyen.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vianh.blogtruyen.data.local.dao.CategoryDao
import com.vianh.blogtruyen.data.local.dao.ChapterDao
import com.vianh.blogtruyen.data.local.dao.HistoryDao
import com.vianh.blogtruyen.data.local.dao.MangaDao
import com.vianh.blogtruyen.data.local.entity.*
import kotlinx.coroutines.*

@Database(
    entities = [
        MangaEntity::class,
        ChapterEntity::class,
        MangaCategory::class,
        CategoryEntity::class,
        HistoryEntity::class
    ],
    exportSchema = false,
    version = 1
)
abstract class MangaDb : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun categoryDao(): CategoryDao
    abstract fun chapterDao(): ChapterDao
    abstract fun historyDao(): HistoryDao

    companion object {
        private const val DB_NAME = "MangaDB.db"

        // Todo: finish this list
        val categories: List<CategoryEntity> by lazy {
            listOf(
                CategoryEntity("action", "Action", "/theloai/action"),
                CategoryEntity("Adventure", "Adventure", "/theloai/adventure"),
                CategoryEntity("Comedy", "Comedy", "/theloai/comedy"),
                CategoryEntity("Shounnen", "Shounen", "/theloai/shounen"),
                CategoryEntity("Magic", "Magic", "/theloai/magic"),
                CategoryEntity("Fantasy", "Fantasy", "/theloai/adventure/fantasy-new"),
                CategoryEntity("Manga", "Manga", "/theloai/manga"),
                CategoryEntity("FullColor", "Full màu", "/theloai/full-mau")
            )
        }

        fun provideDb(context: Context): MangaDb {
            var mangaDb: MangaDb? = null
            val callback = object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    mangaDb?.let {
                        GlobalScope.launch(Dispatchers.IO) {
                            it.categoryDao().upsert(categories)
                        }
                    }
                }
            }

            mangaDb = Room.databaseBuilder(context, MangaDb::class.java, DB_NAME)
                .addCallback(callback)
                .build()
            return mangaDb
        }
    }
}