package com.obrien.thecathedral.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.obrien.thecathedral.model.JournalEntry
import com.obrien.thecathedral.model.WeeklyReview
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntry)

    @Query("SELECT * FROM journal_entries ORDER BY date DESC")
    fun getAllEntriesFlow(): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE date = :date")
    suspend fun getEntryByDate(date: String): JournalEntry?

    @Query("DELETE FROM journal_entries WHERE date = :date")
    suspend fun deleteEntryByDate(date: String)

    @Query("SELECT * FROM journal_entries ORDER BY date DESC LIMIT 7")
    fun getLastSevenDaysFlow(): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyReview(review: WeeklyReview)

    @Query("SELECT * FROM weekly_reviews ORDER BY date DESC")
    fun getAllWeeklyReviewsFlow(): Flow<List<WeeklyReview>>
}
