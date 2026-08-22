package com.obrien.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "homework_entries")
data class HomeworkEntry(
    @PrimaryKey val id: String,
    val date: String, // yyyy-MM-dd
    val subject: String,
    val description: String = "",
    val whatILearned: String = "",
    val isCompleted: Boolean = false
)
