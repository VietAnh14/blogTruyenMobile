package com.vianh.blogtruyen.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vianh.blogtruyen.MvvmApp
import com.vianh.blogtruyen.data.model.Category
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.data.model.MangaCategory
import kotlinx.coroutines.*

@Database(
    entities = [Manga::class, Chapter::class, MangaCategory::class, Category::class],
    exportSchema = false,
    version = 1
)
abstract class MangaDb : RoomDatabase() {
    abstract fun mangaDao(): MangaDao

    companion object {
        private const val dbName = "MangaDB.db"
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
        val db: MangaDb by lazy {
            Room.databaseBuilder(MvvmApp.app, MangaDb::class.java, dbName)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(spDb: SupportSQLiteDatabase) {
                        super.onCreate(spDb)
                        CoroutineScope(Dispatchers.IO).launch {
                            db.mangaDao().inserListCategory(categories)
                        }
                    }
                })
                .build()
        }
    }
}