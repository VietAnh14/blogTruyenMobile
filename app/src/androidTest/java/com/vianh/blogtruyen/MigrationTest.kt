package com.vianh.blogtruyen

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vianh.blogtruyen.data.db.MangaDb
import com.vianh.blogtruyen.data.db.Migrations
import com.vianh.blogtruyen.data.db.entity.ChapterEntity
import com.vianh.blogtruyen.data.db.entity.MangaEntity
import com.vianh.blogtruyen.data.model.Chapter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"

    @Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MangaDb::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db = helper.createDatabase(TEST_DB, 1).apply {
            // db has schema version 1. insert some data using SQL queries.
            // You cannot use DAO classes because they expect the latest schema.
            execSQL("INSERT INTO ${MangaEntity.TABLE_NAME} VALUES('', '', '', '', '', 12345)")
            execSQL("INSERT INTO ${ChapterEntity.TABLE_NAME} VALUES('', '', '', 12345, 1, false)")
            // Prepare for the next version.
            close()
        }

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, Migrations.MIGRATION_1_2)
        db.execSQL("SELECT * FROM chapters")
        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
    }
}