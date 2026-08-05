package com.example.thecathedral.data

import com.example.thecathedral.model.Alarm
import com.example.thecathedral.model.Pillar
import java.time.LocalTime

object ScheduleData {
    val pillars = listOf(
        Pillar(
            id = "awakening",
            name = "THE AWAKENING",
            timeRange = "07:00 - 07:30",
            alarms = listOf(
                Alarm(
                    id = "ignition",
                    time = LocalTime.of(7, 0),
                    name = "IGNITION",
                    tasks = listOf("Sit up immediately. No snooze.", "Drink 500ml water.", "2 minutes direct sunlight in eyes.", "Read purpose out loud.")
                ),
                Alarm(
                    id = "grounding",
                    time = LocalTime.of(7, 15),
                    name = "THE GROUNDING",
                    tasks = listOf("Cold splash on face.", "Dress in real clothes.", "Write one sentence: 'Today I will conquer ______.'")
                )
            )
        ),
        Pillar(
            id = "forge",
            name = "THE FORGE - TECHNE",
            timeRange = "07:30 - 12:00",
            alarms = listOf(
                Alarm(
                    id = "deep_work_1",
                    time = LocalTime.of(7, 30),
                    name = "DEEP WORK BLOCK 1",
                    tasks = listOf("Phone on Do Not Disturb", "Headphones on", "Write code")
                ),
                Alarm(
                    id = "theory",
                    time = LocalTime.of(9, 0),
                    name = "THE THEORY HOUR",
                    tasks = listOf("Breakfast + CS concept", "Watch 15-min tutorial", "45 mins structured study")
                ),
                Alarm(
                    id = "deep_work_2",
                    time = LocalTime.of(10, 0),
                    name = "DEEP WORK BLOCK 2",
                    tasks = listOf("Build 'Liberty Timeline' project", "One working feature", "10 min break every 50 mins")
                ),
                Alarm(
                    id = "commit",
                    time = LocalTime.of(11, 45),
                    name = "THE COMMIT RITUAL",
                    tasks = listOf("git add . git commit -m 'daily progress' git push", "Close the laptop")
                )
            )
        ),
        Pillar(
            id = "archive",
            name = "THE ARCHIVE - HISTORIA",
            timeRange = "12:00 - 13:30",
            alarms = listOf(
                Alarm(
                    id = "nourishment",
                    time = LocalTime.of(12, 0),
                    name = "NOURISHMENT",
                    tasks = listOf("Lunch. Screens off. Eat slowly.")
                ),
                Alarm(
                    id = "primary_source",
                    time = LocalTime.of(12, 30),
                    name = "THE PRIMARY SOURCE",
                    tasks = listOf("Read ONE section (5-10 pages)", "Ask teaching about human nature", "Write in journal")
                ),
                Alarm(
                    id = "peripatetic",
                    time = LocalTime.of(13, 0),
                    name = "THE PERIPATETIC WALK",
                    tasks = listOf("Walk outside. No phone.", "Let the mind wander.")
                )
            )
        ),
        Pillar(
            id = "afternoon_grind",
            name = "THE AFTERNOON GRIND",
            timeRange = "13:30 - 16:00",
            alarms = listOf(
                Alarm(
                    id = "admin",
                    time = LocalTime.of(13, 30),
                    name = "THE ADMIN",
                    tasks = listOf("Check emails, LinkedIn, Slack", "Reply to messages", "Update progress log")
                ),
                Alarm(
                    id = "bug_hunt",
                    time = LocalTime.of(14, 0),
                    name = "THE BUG HUNT",
                    tasks = listOf("Tackle bug from sticky note", "ADHD Hack: '5-Minute Bargain'")
                ),
                Alarm(
                    id = "community",
                    time = LocalTime.of(15, 30),
                    name = "THE COMMUNITY",
                    tasks = listOf("Open Source or Forum", "Read one GitHub issue", "Reply to a beginner's question")
                )
            )
        ),
        Pillar(
            id = "arena",
            name = "THE ARENA - GYMNOS",
            timeRange = "16:00 - 18:00",
            alarms = listOf(
                Alarm(
                    id = "physical",
                    time = LocalTime.of(16, 0),
                    name = "PHYSICAL FORTITUDE",
                    tasks = listOf("Laptop closed. Phone left behind.", "30 mins elevated heart rate", "Sweat hard")
                ),
                Alarm(
                    id = "playground",
                    time = LocalTime.of(17, 0),
                    name = "THE PLAYGROUND",
                    tasks = listOf("Open editor with ZERO pressure", "FOR JOY", "Tinker, sketch, or write physics simulation")
                )
            )
        ),
        Pillar(
            id = "sanctuary",
            name = "THE SANCTUARY - SOPHIA",
            timeRange = "18:00 - 22:00",
            alarms = listOf(
                Alarm(
                    id = "evening_recovery",
                    time = LocalTime.of(18, 0),
                    name = "EVENING RECOVERY",
                    tasks = listOf("Dinner. Socialize.", "Computer stays closed.")
                ),
                Alarm(
                    id = "scholar_compass",
                    time = LocalTime.of(19, 30),
                    name = "THE SCHOLAR'S COMPASS",
                    tasks = listOf("Plan Tomorrow", "Rotating Curriculum (Science, Lit, Logic, Art, Theology)")
                ),
                Alarm(
                    id = "buffer_zone",
                    time = LocalTime.of(20, 15),
                    name = "THE BUFFER ZONE",
                    tasks = listOf("Catch-up or flex block", "25-min Pomodoro if needed", "Read fiction or rest")
                ),
                Alarm(
                    id = "digital_sunset",
                    time = LocalTime.of(21, 0),
                    name = "DIGITAL SUNSET",
                    tasks = listOf("Phone on counter to charge", "Close laptop", "Primary Philosophy reading", "Journaling (Win, Learn, Better)")
                )
            )
        )
    )

    const val PURPOSE_STATEMENT = "My purpose is to become so disciplined that I can use technology to defend the truth of history and the dignity of free men."
    const val MANTRA = "I am one of many. But I am the master of my own two hands. Today I will build. Tomorrow I will build again. That is my direction. That is my peace. That is my freedom."
}
