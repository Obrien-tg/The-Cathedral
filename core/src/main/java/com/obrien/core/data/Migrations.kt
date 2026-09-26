package com.obrien.core.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database migrations.
 *
 * IMPORTANT: If you are starting fresh or don't have users on older versions,
 * you can set database version to 1 and remove these migrations.
 * Otherwise, implement the actual schema changes that occurred between versions.
 */

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Example: Adding a new column to journal_entries
        // db.execSQL("ALTER TABLE journal_entries ADD COLUMN new_field INTEGER DEFAULT 0")

        // If you had actual schema changes from v1 to v2, add them here.
        // For now, this is a no-op placeholder.
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Example: Creating a new table
        // db.execSQL(
        //     "CREATE TABLE IF NOT EXISTS homework_entries (" +
        //     "id TEXT PRIMARY KEY NOT NULL, " +
        //     "date TEXT NOT NULL, " +
        //     "subject TEXT NOT NULL, " +
        //     "description TEXT NOT NULL DEFAULT '', " +
        //     "whatILearned TEXT NOT NULL DEFAULT '', " +
        //     "isCompleted INTEGER NOT NULL DEFAULT 0)"
        // )

        // If you had actual schema changes from v2 to v3, add them here.
        // For now, this is a no-op placeholder.
    }
}
