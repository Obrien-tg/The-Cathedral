package com.obrien.core.model

import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Serializable
data class WeeklyIntention(
    val weekStartDate: String = "",
    val weeklyAim: String = "",
    val techneFocus: String = "",
    val historiaBook: String = "",
    val historiaResearch: String = "",
    val gymnosFocus: String = "",
    val sophiaTheme: String = "",
    val subjectFocus: String = "",
    val bodyFocus: String = "",
    val characterAim: String = ""
) {
    val hasAnyFocus: Boolean
        get() = listOf(
            weeklyAim, techneFocus, historiaBook, historiaResearch,
            gymnosFocus, sophiaTheme, subjectFocus, bodyFocus, characterAim
        ).any { it.isNotBlank() }

    fun isActiveForCurrentWeek(): Boolean {
        if (weekStartDate.isBlank() || !hasAnyFocus) return false
        return weekStartDate == currentWeekStart()
    }

    companion object {
        fun currentWeekStart(): String =
            LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toString()

        fun emptyForCurrentWeek(): WeeklyIntention =
            WeeklyIntention(weekStartDate = currentWeekStart())
    }
}
