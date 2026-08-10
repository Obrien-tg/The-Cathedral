package com.obrien.thecathedral.model

import kotlinx.serialization.Serializable

@Serializable
data class WeeklyIntention(
    /** ISO date of the Monday that starts this week (yyyy-MM-dd) */
    val weekStart: String,
    val techneProject: String = "",
    val techneSkill: String = "",
    val historiaBook: String = "",
    val historiaTopic: String = "",
    val gymnosFocus: String = "",
    val sophiaTheme: String = "",
    val weekNote: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun isEmpty(): Boolean =
        techneProject.isBlank() &&
        techneSkill.isBlank() &&
        historiaBook.isBlank() &&
        historiaTopic.isBlank() &&
        gymnosFocus.isBlank() &&
        sophiaTheme.isBlank() &&
        weekNote.isBlank()
}
