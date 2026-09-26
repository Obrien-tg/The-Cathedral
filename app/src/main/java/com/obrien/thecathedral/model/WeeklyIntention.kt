package com.obrien.thecathedral.model

import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * What you are deliberately forming this week.
 * Empty fields mean "use the default schedule text".
 */
@Serializable
data class WeeklyIntention(
    /** Week start date yyyy-MM-dd (Monday recommended) */
    val weekStartDate: String = "",
    /** TECHNE — project / codebase / feature you are building */
    val techneFocus: String = "",
    /** HISTORIA — book, primary source, or body of text */
    val historiaBook: String = "",
    /** HISTORIA — person, era, or research question */
    val historiaResearch: String = "",
    /** GYMNOS — physical emphasis (strength, run, mobility…) */
    val gymnosFocus: String = "",
    /** SOPHIA — virtue, question, or evening theme */
    val sophiaTheme: String = "",
    /** Optional one-line intention for the whole week */
    val weeklyAim: String = ""
) {
    val hasAnyFocus: Boolean
        get() = listOf(
            techneFocus, historiaBook, historiaResearch,
            gymnosFocus, sophiaTheme, weeklyAim
        ).any { it.isNotBlank() }

    /** True only if this intention belongs to the current calendar week (Mon-start). */
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
