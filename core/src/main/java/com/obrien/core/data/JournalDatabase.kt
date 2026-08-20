package com.obrien.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.obrien.core.model.JournalEntry
import com.obrien.core.model.WeeklyReview

@Database(entities = [JournalEntry::class, WeeklyReview::class], version = 2, exportSchema = false)
abstract class JournalDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
}
