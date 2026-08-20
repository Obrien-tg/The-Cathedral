package com.obrien.core.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey
    val date: String, // ISO-8601: yyyy-MM-dd
    val morningCompleted: Boolean = false,
    val schoolCompleted: Boolean = false,
    val resetCompleted: Boolean = false,
    val studyCompleted: Boolean = false,
    val bodyCompleted: Boolean = false,
    val eveningCompleted: Boolean = false,
    val mindEffort: Boolean = false,
    val bodyEffort: Boolean = false,
    val characterEffort: Boolean = false,
    val wentWell: String = "",
    val hardPart: String = "",
    val gratitude: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    @Ignore
    val score: Int = listOf(
        morningCompleted, schoolCompleted, resetCompleted,
        studyCompleted, bodyCompleted, eveningCompleted
    ).count { it }
    
    @Ignore
    val totalScore: Int = 6
}
