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
    val characterAim: String = "",
    // Quest of the Week — "things to try", free-text, comma-separated or plain
    val explorations: String = ""
) {
    val hasAnyFocus: Boolean
        get() = listOf(
            weeklyAim, techneFocus, historiaBook, historiaResearch,
            gymnosFocus, sophiaTheme, subjectFocus, bodyFocus, characterAim,
            explorations
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

        fun nextWeekStart(): String =
            LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .toString()

        /**
         * The week a newly saved intention belongs to.
         * On Sunday, planning happens for the week that begins tomorrow —
         * stamping the current (ending) week would silently expire overnight.
         */
        fun weekStartForSave(): String =
            if (LocalDate.now().dayOfWeek == DayOfWeek.SUNDAY) nextWeekStart()
            else currentWeekStart()

        fun emptyForCurrentWeek(): WeeklyIntention =
            WeeklyIntention(weekStartDate = weekStartForSave())
    }
}
