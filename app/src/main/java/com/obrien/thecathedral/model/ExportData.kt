package com.obrien.thecathedral.model

import com.obrien.core.model.JournalEntry
import com.obrien.core.model.WeeklyReview
import kotlinx.serialization.Serializable

@Serializable
data class ExportData(
    val wakeTime: String,
    val historicalCompletions: Map<String, Int>,
    val totalFocusSessions: Int,
    val journalEntries: List<JournalEntry>,
    val weeklyReviews: List<WeeklyReview>,
    val exportTimestamp: Long = System.currentTimeMillis()
)
