package com.vianh.blogtruyen.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vianh.blogtruyen.data.local.dao.CategoryDao
import com.vianh.blogtruyen.data.local.dao.ChapterDao
import com.vianh.blogtruyen.features.history.data.HistoryDao
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
    abstract val mangaDao: MangaDao
    abstract val categoryDao: CategoryDao
    abstract val chapterDao: ChapterDao
    abstract val historyDao: HistoryDao

    companion object {
        private const val DB_NAME = "MangaDB.db"

//        // Todo: finish this list
//        val categories: List<CategoryEntity> by lazy {
//            listOf(
//                CategoryEntity("Action", "/theloai/action"),
//                CategoryEntity("Adventure", "/theloai/adventure"),
//                CategoryEntity("Comedy", "/theloai/comedy"),
//                CategoryEntity("Shounen", "/theloai/shounen"),
//                CategoryEntity("Magic", "/theloai/magic"),
//                CategoryEntity("Fantasy", "/theloai/adventure/fantasy-new"),
//                CategoryEntity("Manga", "/theloai/manga"),
//                CategoryEntity("Full màu", "/theloai/full-mau")
//            )
//        }

        fun provideDb(context: Context): MangaDb {
            var mangaDb: MangaDb? = null
            val callback = object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    mangaDb?.let {
                        GlobalScope.launch(Dispatchers.IO) {
//                            it.categoryDao().insert(categories)
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