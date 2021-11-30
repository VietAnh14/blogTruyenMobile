package com.vianh.blogtruyen.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    val MIGRATION_1_2: Migration
        get() = object: Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE chapters ADD uploadDate INTEGER NOT NULL DEFAULT 0")
            }
        }

}