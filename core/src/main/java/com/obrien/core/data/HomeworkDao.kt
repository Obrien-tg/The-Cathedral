package com.obrien.core.data

import androidx.room.*
import com.obrien.core.model.HomeworkEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeworkDao {
    @Query("SELECT * FROM homework_entries WHERE date = :date")
    fun getHomeworkForDate(date: String): Flow<List<HomeworkEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HomeworkEntry)

    @Delete
    suspend fun delete(entry: HomeworkEntry)

    @Query("UPDATE homework_entries SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompletion(id: String, completed: Boolean)
}
