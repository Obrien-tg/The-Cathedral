package com.obrien.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.obrien.core.model.JournalEntry
import com.obrien.core.model.WeeklyReview
import com.obrien.core.model.HomeworkEntry

@Database(
    entities = [JournalEntry::class, WeeklyReview::class, HomeworkEntry::class],
    version = 3,
    exportSchema = false
)
abstract class JournalDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun homeworkDao(): HomeworkDao
}
