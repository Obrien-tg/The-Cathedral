package com.example.thecathedral.model

import kotlinx.serialization.Serializable

@Serializable
data class JournalEntry(
    val id: String,
    val date: String, // ISO-8601: yyyy-MM-dd
    val techneCompleted: Boolean = false,
    val historiaCompleted: Boolean = false,
    val gymnosoCompleted: Boolean = false,
    val sophiaCompleted: Boolean = false,
    val freeText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
