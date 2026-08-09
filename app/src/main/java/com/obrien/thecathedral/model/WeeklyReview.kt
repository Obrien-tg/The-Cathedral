package com.obrien.thecathedral.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "weekly_reviews")
data class WeeklyReview(
    @PrimaryKey
    val date: String, // Sunday date: yyyy-MM-dd
    val victory: String,
    val failure: String,
    val adjustment: String,
    val timestamp: Long = System.currentTimeMillis()
)
