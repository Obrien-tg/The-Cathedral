package com.obrien.thelantern.data

import com.obrien.core.model.Alarm
import com.obrien.core.model.Pillar
import com.obrien.core.model.WeeklyIntention

/**
 * Takes the fixed Rule of Life and overlays this week's living context
 * so task text names the actual focus areas.
 */
object ScheduleShaper {

    fun shape(intention: WeeklyIntention, basePillars: List<Pillar> = ScheduleData.pillars): List<Pillar> {
        if (!intention.isActiveForCurrentWeek()) return basePillars

        return basePillars.map { pillar ->
            pillar.copy(
                alarms = pillar.alarms.map { alarm ->
                    alarm.copy(tasks = shapeTasks(alarm, intention))
                }
            )
        }
    }

    private fun shapeTasks(alarm: Alarm, i: WeeklyIntention): List<String> {
        val base = alarm.tasks
        return when (alarm.id) {
            "ready" -> {
                if (i.weeklyAim.isNotBlank()) {
                    listOf(
                        "Uniform, bag, homework folder",
                        "Focus: ${i.weeklyAim.trim()}",
                        "One sentence: 'Today I will ______.'"
                    )
                } else base
            }

            "deep_study_1", "deep_study_2" -> {
                if (i.subjectFocus.isNotBlank()) {
                    val aimText = if (i.weeklyAim.isNotBlank()) " — ${i.weeklyAim.trim()}" else ""
                    listOf("First focus: ${i.subjectFocus.trim()}$aimText")
                } else base
            }

            "move" -> {
                if (i.bodyFocus.isNotBlank()) {
                    listOf("Move: ${i.bodyFocus.trim()}")
                } else base
            }

            "read" -> {
                if (i.historiaBook.isNotBlank()) {
                    listOf("Read: ${i.historiaBook.trim()}")
                } else base
            }

            "journal_line" -> {
                if (i.characterAim.isNotBlank()) {
                    listOf(
                        "One journal line",
                        "Reflect on: ${i.characterAim.trim()}"
                    )
                } else base
            }

            else -> base
        }
    }

    /** Short labels for Home / headers */
    fun focusSummary(intention: WeeklyIntention): List<Pair<String, String>> {
        val rows = mutableListOf<Pair<String, String>>()
        if (intention.weeklyAim.isNotBlank()) rows += "AIM" to intention.weeklyAim.trim()
        if (intention.subjectFocus.isNotBlank()) rows += "SUBJECT" to intention.subjectFocus.trim()
        if (intention.historiaBook.isNotBlank()) rows += "BOOK" to intention.historiaBook.trim()
        if (intention.bodyFocus.isNotBlank()) rows += "BODY" to intention.bodyFocus.trim()
        if (intention.characterAim.isNotBlank()) rows += "CHARACTER" to intention.characterAim.trim()
        return rows
    }
}
