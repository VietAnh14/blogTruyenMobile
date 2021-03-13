package com.vianh.blogtruyen.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vianh.blogtruyen.MvvmApp
import com.vianh.blogtruyen.data.local.entity.Category
import com.vianh.blogtruyen.data.local.entity.Chapter
import com.vianh.blogtruyen.data.local.entity.Manga
import com.vianh.blogtruyen.data.local.entity.MangaCategory
import kotlinx.coroutines.*

@Database(
    entities = [Manga::class, Chapter::class, MangaCategory::class, Category::class],
    exportSchema = false,
    version = 1
)
abstract class MangaDb : RoomDatabase() {
    abstract fun mangaDao(): MangaDao

    companion object {
        private const val DB_NAME = "MangaDB.db"

        // Todo: finish this list
        val categories: List<Category> by lazy {
            listOf(
                Category("action", "Action", "/theloai/action"),
                Category("Adventure", "Adventure", "/theloai/adventure"),
                Category("Comedy", "Comedy", "/theloai/comedy"),
                Category("Shounnen", "Shounen", "/theloai/shounen"),
                Category("Magic", "Magic", "/theloai/magic"),
                Category("Fantasy", "Fantasy", "/theloai/adventure/fantasy-new"),
                Category("Manga", "Manga", "/theloai/manga"),
                Category("FullColor", "Full màu", "/theloai/full-mau")
            )
        }

        fun provideDb(context: Context): MangaDb {
            // Todo: finish this list
            val categories: List<Category> by lazy {
                listOf(
                    Category("action", "Action", "/theloai/action"),
                    Category("Adventure", "Adventure", "/theloai/adventure"),
                    Category("Comedy", "Comedy", "/theloai/comedy"),
                    Category("Shounnen", "Shounen", "/theloai/shounen"),
                    Category("Magic", "Magic", "/theloai/magic"),
                    Category("Fantasy", "Fantasy", "/theloai/adventure/fantasy-new"),
                    Category("Manga", "Manga", "/theloai/manga"),
                    Category("FullColor", "Full màu", "/theloai/full-mau")
                )
            }

            var mangaDb: MangaDb? = null
            val callback = object: RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    mangaDb?.let {
                        GlobalScope.launch(Dispatchers.IO) {
                            it.mangaDao().inserListCategory(categories)
                        }
                    }
                }
            }

            mangaDb = Room.databaseBuilder(context, MangaDb::class.java, DB_NAME)
                .addCallback(callback)
                .build()
            return mangaDb
        }

        val db: MangaDb by lazy {
            Room.databaseBuilder(MvvmApp.app, MangaDb::class.java, DB_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(spDb: SupportSQLiteDatabase) {
                        super.onCreate(spDb)
                        GlobalScope.launch(Dispatchers.IO) {
                            db.mangaDao().inserListCategory(categories)
                        }
                    }
                })
                .build()
        }
    }
}