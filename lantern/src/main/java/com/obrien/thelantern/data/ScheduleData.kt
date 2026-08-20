package com.obrien.thelantern.data

import com.obrien.core.model.Alarm
import com.obrien.core.model.Pillar
import java.time.LocalTime

object ScheduleData {
    val pillars = listOf(
        Pillar(
            id = "morning",
            name = "MORNING START",
            timeRange = "06:30 - 07:20",
            alarms = listOf(
                Alarm(
                    id = "wake",
                    time = LocalTime.of(6, 30),
                    name = "WAKE UP",
                    tasks = listOf("Up on time; no long snooze", "Drink water")
                ),
                Alarm(
                    id = "ready",
                    time = LocalTime.of(7, 0),
                    name = "GET READY",
                    tasks = listOf("Uniform, bag, homework folder", "One sentence: 'Today I will ______.'")
                )
            )
        ),
        Pillar(
            id = "school",
            name = "SCHOOL DAY",
            timeRange = "07:30 - 13:30",
            alarms = listOf(
                Alarm(
                    id = "present",
                    time = LocalTime.of(7, 30),
                    name = "PRESENT",
                    tasks = listOf("I showed up and tried in my classes")
                ),
                Alarm(
                    id = "learned",
                    time = LocalTime.of(13, 0),
                    name = "LEARNED",
                    tasks = listOf("I can name one thing I learned today")
                ),
                Alarm(
                    id = "courage_kind",
                    time = LocalTime.of(13, 20),
                    name = "COURAGE & KINDNESS",
                    tasks = listOf("One moment of courage or kindness")
                )
            )
        ),
        Pillar(
            id = "reset",
            name = "AFTER-SCHOOL RESET",
            timeRange = "13:30 - 14:30",
            alarms = listOf(
                Alarm(
                    id = "land",
                    time = LocalTime.of(13, 40),
                    name = "LAND",
                    tasks = listOf("Snack, short rest, change if possible", "Phone away this block")
                ),
                Alarm(
                    id = "plan",
                    time = LocalTime.of(14, 15),
                    name = "PLAN",
                    tasks = listOf("Choose homework order (hard subject first)")
                )
            )
        ),
        Pillar(
            id = "study",
            name = "STUDY FORGE",
            timeRange = "14:30 - 16:30",
            alarms = listOf(
                Alarm(
                    id = "deep_study_1",
                    time = LocalTime.of(14, 30),
                    name = "DEEP STUDY 1",
                    tasks = listOf("Hard subject first (shaped by weekly focus)")
                ),
                Alarm(
                    id = "deep_study_2",
                    time = LocalTime.of(15, 30),
                    name = "DEEP STUDY 2",
                    tasks = listOf("Second subject / remaining homework")
                ),
                Alarm(
                    id = "bag_tomorrow",
                    time = LocalTime.of(16, 20),
                    name = "BAG PACKED",
                    tasks = listOf("Bag packed for tomorrow; desk clear")
                )
            )
        ),
        Pillar(
            id = "body",
            name = "BODY & BELONGING",
            timeRange = "16:30 - 19:00",
            alarms = listOf(
                Alarm(
                    id = "move",
                    time = LocalTime.of(17, 0),
                    name = "MOVE",
                    tasks = listOf("Sport, walk, stretch, or outdoor play")
                ),
                Alarm(
                    id = "belong",
                    time = LocalTime.of(18, 0),
                    name = "BELONG",
                    tasks = listOf("Help at home or good time with family")
                )
            )
        ),
        Pillar(
            id = "evening",
            name = "EVENING CLOSE",
            timeRange = "19:30 - 21:30",
            alarms = listOf(
                Alarm(
                    id = "screens_down",
                    time = LocalTime.of(20, 0),
                    name = "SCREENS DOWN",
                    tasks = listOf("Screens down at agreed time")
                ),
                Alarm(
                    id = "read",
                    time = LocalTime.of(20, 30),
                    name = "READ",
                    tasks = listOf("Reading from this week’s book")
                ),
                Alarm(
                    id = "journal_line",
                    time = LocalTime.of(21, 0),
                    name = "JOURNAL",
                    tasks = listOf("One journal line")
                ),
                Alarm(
                    id = "sleep",
                    time = LocalTime.of(21, 30),
                    name = "SLEEP",
                    tasks = listOf("Sleep on time")
                )
            )
        )
    )

    const val PURPOSE_STATEMENT = "I learn to keep my word, use my mind well, care for my body, and treat people with dignity — so I can build a life that is truly my own."
    const val MANTRA = "I am still becoming. Today I show up. Tomorrow I show up again. That is how greatness begins."

    val SUBJECTS = listOf(
        "Mathematics", "English", "Afrikaans", "Economic and Management Sciences (EMS)",
        "Social Sciences", "Technology", "Natural Sciences", "Life Orientation (LO)",
        "Creative Arts", "Other"
    )
}
