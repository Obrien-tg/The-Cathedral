package com.obrien.thecathedral.data

import com.obrien.core.model.Alarm
import com.obrien.core.model.Pillar
import com.obrien.core.model.WeeklyIntention

/**
 * Takes the fixed Rule of Life and overlays this week's living context
 * so task text names the actual project, book, and research focus.
 */
object ScheduleShaper {

    fun shape(intention: WeeklyIntention): List<Pillar> {
        if (!intention.isActiveForCurrentWeek()) return ScheduleData.pillars

        return ScheduleData.pillars.map { pillar ->
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
            "grounding" -> {
                if (i.weeklyAim.isNotBlank()) {
                    listOf(
                        "Cold splash on face.",
                        "Dress in real clothes.",
                        "Write one sentence: 'Today I advance — ${i.weeklyAim.trim()}.'"
                    )
                } else base
            }

            "deep_work_1", "deep_work_2" -> {
                if (i.techneFocus.isNotBlank()) {
                    listOf(
                        "Phone on Do Not Disturb",
                        "Headphones on",
                        "Build: ${i.techneFocus.trim()}",
                        "One concrete advance before you rise"
                    )
                } else base
            }

            "theory" -> {
                if (i.techneFocus.isNotBlank()) {
                    listOf(
                        "Breakfast + concept linked to: ${i.techneFocus.trim()}",
                        "15-min focused study or tutorial",
                        "45 mins structured study"
                    )
                } else base
            }

            "commit" -> {
                if (i.techneFocus.isNotBlank()) {
                    listOf(
                        "git add . && git commit -m 'progress: ${i.techneFocus.trim()}' && git push",
                        "Close the laptop"
                    )
                } else base
            }

            "primary_source" -> {
                val book = i.historiaBook.trim()
                val research = i.historiaResearch.trim()
                when {
                    book.isNotBlank() && research.isNotBlank() -> listOf(
                        "Read from: $book",
                        "Hold in mind: $research",
                        "Note one passage that resists easy summary"
                    )
                    book.isNotBlank() -> listOf(
                        "Read from: $book",
                        "Underline one claim",
                        "Write one sentence in your own words"
                    )
                    research.isNotBlank() -> listOf(
                        "Research focus: $research",
                        "Primary evidence only — no secondary summary first",
                        "Capture one fact with its source"
                    )
                    else -> base
                }
            }

            "peripatetic" -> {
                val focus = i.historiaResearch.ifBlank { i.historiaBook }.trim()
                if (focus.isNotBlank()) {
                    listOf(
                        "Walk without headphones",
                        "Turn over: $focus",
                        "Return with one clearer question"
                    )
                } else base
            }

            "physical" -> {
                if (i.gymnosFocus.isNotBlank()) {
                    listOf(
                        "Train with emphasis: ${i.gymnosFocus.trim()}",
                        "Full effort, honest form",
                        "Log how the body answered"
                    )
                } else base
            }

            "scholar_compass" -> {
                val theme = i.sophiaTheme.trim()
                val book = i.historiaBook.trim()
                when {
                    theme.isNotBlank() && book.isNotBlank() -> listOf(
                        "Evening theme: $theme",
                        "If reading: a page of $book",
                        "Close with one line in the journal"
                    )
                    theme.isNotBlank() -> listOf(
                        "Evening theme: $theme",
                        "Ask: did today serve this?",
                        "One line in the journal"
                    )
                    book.isNotBlank() -> listOf(
                        "Quiet pages: $book",
                        "No performance — only attention",
                        "One line in the journal"
                    )
                    else -> base
                }
            }

            "digital_sunset" -> {
                if (i.sophiaTheme.isNotBlank() || i.weeklyAim.isNotBlank()) {
                    val line = i.sophiaTheme.ifBlank { i.weeklyAim }.trim()
                    listOf(
                        "Screens down",
                        "Recall the week's aim: $line",
                        "Sleep as discipline, not collapse"
                    )
                } else base
            }

            "admin" -> {
                if (i.weeklyAim.isNotBlank()) {
                    listOf(
                        "Check communications.",
                        "Priority check: ${i.weeklyAim.trim()}",
                        "Update your progress log"
                    )
                } else base
            }

            "bug_hunt" -> {
                if (i.techneFocus.isNotBlank()) {
                    listOf(
                        "Tackle debt in: ${i.techneFocus.trim()}",
                        "Specific correction only — no new features",
                        "ADHD Hack: '5-Minute Bargain'"
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
        if (intention.techneFocus.isNotBlank()) rows += "TECHNE" to intention.techneFocus.trim()
        if (intention.historiaBook.isNotBlank()) rows += "BOOK" to intention.historiaBook.trim()
        if (intention.historiaResearch.isNotBlank()) rows += "RESEARCH" to intention.historiaResearch.trim()
        if (intention.gymnosFocus.isNotBlank()) rows += "GYMNOS" to intention.gymnosFocus.trim()
        if (intention.sophiaTheme.isNotBlank()) rows += "SOPHIA" to intention.sophiaTheme.trim()
        return rows
    }
}
