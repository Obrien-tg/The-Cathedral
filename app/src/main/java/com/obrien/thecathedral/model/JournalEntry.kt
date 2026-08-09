package com.obrien.thecathedral.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey
    val date: String, // ISO-8601: yyyy-MM-dd
    val techneCompleted: Boolean = false,
    val historiaCompleted: Boolean = false,
    val gymnosoCompleted: Boolean = false,
    val sophiaCompleted: Boolean = false,
    val freeText: String = "",
    val learning: String = "",
    val improvement: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    @Ignore
    val score: Int = listOf(techneCompleted, historiaCompleted, gymnosoCompleted, sophiaCompleted).count { it }
}
