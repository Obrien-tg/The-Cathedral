package com.obrien.thelantern.data

import com.obrien.core.model.Alarm
import com.obrien.core.model.Pillar
import java.time.DayOfWeek
import java.time.LocalTime

object ScheduleData {
    
    fun getPillarsForDay(dayOfWeek: DayOfWeek): List<Pillar> = when (dayOfWeek) {
        DayOfWeek.SATURDAY -> saturdayPillars
        DayOfWeek.SUNDAY -> sundayPillars
        else -> schoolDayPillars
    }

    private val schoolDayPillars = listOf(
        Pillar(
            id = "morning",
            name = "MORNING START",
            timeRange = "06:30 - 07:20",
            alarms = listOf(
                Alarm(id = "wake", time = LocalTime.of(6, 30), name = "WAKE UP", tasks = listOf("Up on time; no long snooze", "Drink water")),
                Alarm(id = "ready", time = LocalTime.of(7, 0), name = "GET READY", tasks = listOf("Uniform, bag, homework folder", "One sentence: 'Today I will ______.'"))
            )
        ),
        Pillar(
            id = "school",
            name = "SCHOOL DAY",
            timeRange = "07:30 - 13:30",
            alarms = listOf(
                Alarm(id = "present", time = LocalTime.of(7, 30), name = "PRESENT", tasks = listOf("I showed up and tried in my classes")),
                Alarm(id = "learned", time = LocalTime.of(13, 0), name = "LEARNED", tasks = listOf("I can name one thing I learned today")),
                Alarm(id = "courage_kind", time = LocalTime.of(13, 20), name = "COURAGE & KINDNESS", tasks = listOf("One moment of courage or kindness"))
            )
        ),
        Pillar(
            id = "reset",
            name = "AFTER-SCHOOL RESET",
            timeRange = "13:30 - 14:30",
            alarms = listOf(
                Alarm(id = "land", time = LocalTime.of(13, 40), name = "LAND", tasks = listOf("Snack, short rest, change if possible", "Phone away this block")),
                Alarm(id = "plan", time = LocalTime.of(14, 15), name = "PLAN", tasks = listOf("Choose homework order (hard subject first)"))
            )
        ),
        Pillar(
            id = "study",
            name = "STUDY FORGE",
            timeRange = "14:30 - 16:30",
            alarms = listOf(
                Alarm(id = "deep_study_1", time = LocalTime.of(14, 30), name = "DEEP STUDY 1", tasks = listOf("Hard subject first (shaped by weekly focus)")),
                Alarm(id = "deep_study_2", time = LocalTime.of(15, 30), name = "DEEP STUDY 2", tasks = listOf("Second subject / remaining homework")),
                Alarm(id = "bag_tomorrow", time = LocalTime.of(16, 20), name = "BAG PACKED", tasks = listOf("Bag packed for tomorrow; desk clear"))
            )
        ),
        Pillar(
            id = "body",
            name = "BODY & BELONGING",
            timeRange = "16:30 - 19:00",
            alarms = listOf(
                Alarm(id = "move", time = LocalTime.of(17, 0), name = "MOVE", tasks = listOf("Sport, walk, stretch, or outdoor play")),
                Alarm(id = "belong", time = LocalTime.of(18, 0), name = "BELONG", tasks = listOf("Help at home or good time with family"))
            )
        ),
        Pillar(
            id = "evening",
            name = "EVENING CLOSE",
            timeRange = "19:30 - 21:30",
            alarms = listOf(
                Alarm(id = "screens_down", time = LocalTime.of(20, 0), name = "SCREENS DOWN", tasks = listOf("Screens down at agreed time")),
                Alarm(id = "read", time = LocalTime.of(20, 30), name = "READ", tasks = listOf("Reading from this week’s book")),
                Alarm(id = "journal_line", time = LocalTime.of(21, 0), name = "JOURNAL", tasks = listOf("One journal line")),
                Alarm(id = "sleep", time = LocalTime.of(21, 30), name = "SLEEP", tasks = listOf("Sleep on time"))
            )
        )
    )

    private val saturdayPillars = listOf(
        Pillar(
            id = "morning",
            name = "MORNING START",
            timeRange = "08:00 - 09:00",
            alarms = listOf(
                Alarm(id = "wake", time = LocalTime.of(8, 0), name = "WAKE UP", tasks = listOf("Gently up", "Drink water"))
            )
        ),
        Pillar(
            id = "discovery",
            name = "DISCOVERY BLOCK",
            timeRange = "10:00 - 12:30",
            alarms = listOf(
                Alarm(id = "explore", time = LocalTime.of(10, 0), name = "EXPLORE", tasks = listOf("What are you exploring today?", "Creative arts, coding, nature, or music"))
            )
        ),
        Pillar(
            id = "read",
            name = "FREE READING",
            timeRange = "14:00 - 15:30",
            alarms = listOf(
                Alarm(id = "read_free", time = LocalTime.of(14, 0), name = "READ", tasks = listOf("Read anything for joy"))
            )
        ),
        Pillar(
            id = "body",
            name = "MOVE",
            timeRange = "16:00 - 18:00",
            alarms = listOf(
                Alarm(id = "move", time = LocalTime.of(16, 30), name = "MOVE", tasks = listOf("Walk, cycle, or play outside"))
            )
        ),
        Pillar(
            id = "evening",
            name = "RELAX",
            timeRange = "19:00 - 22:00",
            alarms = listOf(
                Alarm(id = "journal_saturday", time = LocalTime.of(21, 0), name = "JOURNAL", tasks = listOf("Reflect on your discoveries")),
                Alarm(id = "sleep", time = LocalTime.of(22, 0), name = "SLEEP", tasks = listOf("Sleep well"))
            )
        )
    )

    private val sundayPillars = listOf(
        Pillar(
            id = "morning",
            name = "MORNING START",
            timeRange = "08:30 - 09:30",
            alarms = listOf(
                Alarm(id = "wake", time = LocalTime.of(8, 30), name = "WAKE UP", tasks = listOf("Peaceful start"))
            )
        ),
        Pillar(
            id = "rest",
            name = "REST & FAMILY",
            timeRange = "10:00 - 13:00",
            alarms = listOf(
                Alarm(id = "rest_moment", time = LocalTime.of(10, 30), name = "REST", tasks = listOf("Family time, church, or quiet rest"))
            )
        ),
        Pillar(
            id = "review",
            name = "REFLECT",
            timeRange = "15:00 - 16:30",
            alarms = listOf(
                Alarm(id = "review_week", time = LocalTime.of(15, 30), name = "WEEKLY REVIEW", tasks = listOf("How was your week?", "Adjust one thing for next week"))
            )
        ),
        Pillar(
            id = "body_light",
            name = "LIGHT MOVE",
            timeRange = "17:00 - 18:30",
            alarms = listOf(
                Alarm(id = "stretch", time = LocalTime.of(17, 30), name = "STRETCH", tasks = listOf("Gently move your body"))
            )
        ),
        Pillar(
            id = "evening",
            name = "PREPARE",
            timeRange = "19:30 - 21:00",
            alarms = listOf(
                Alarm(id = "prep_school", time = LocalTime.of(20, 0), name = "PREPARE", tasks = listOf("Bag packed for Monday")),
                Alarm(id = "sleep", time = LocalTime.of(21, 0), name = "SLEEP", tasks = listOf("Early to bed"))
            )
        )
    )

    // For backward compatibility / static access where needed
    val pillars = schoolDayPillars

    const val PURPOSE_STATEMENT = "I learn to keep my word, use my mind well, care for my body, and treat people with dignity — so I can build a life that is truly my own."
    const val MANTRA = "I am still becoming. Today I show up. Tomorrow I show up again. That is how greatness begins."

    val SUBJECTS = listOf(
        "Mathematics", "English", "Afrikaans", "Economic and Management Sciences (EMS)",
        "Social Sciences", "Technology", "Natural Sciences", "Life Orientation (LO)",
        "Creative Arts", "Other"
    )
}
