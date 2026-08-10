package com.obrien.thecathedral.domain.usecase

import com.obrien.thecathedral.data.ScheduleData
import com.obrien.thecathedral.data.ScheduleRepository
import com.obrien.thecathedral.model.Pillar
import com.obrien.thecathedral.model.WeeklyIntention
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject

class GetPersonalizedScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    operator fun invoke(): Flow<List<Pillar>> = combine(
        repository.wakeTime,
        repository.weeklyIntention
    ) { wakeTimeStr, intention ->
        val wakeTime = try {
            LocalTime.parse(wakeTimeStr)
        } catch (_: Exception) {
            LocalTime.of(7, 0)
        }

        val baseWakeTime = LocalTime.of(7, 0)
        val offset = Duration.between(baseWakeTime, wakeTime)

        ScheduleData.pillars.map { pillar ->
            val shiftedAlarms = pillar.alarms.map { alarm ->
                val personalizedTasks = if (intention != null) {
                    when (alarm.id) {
                        "deep_work_1", "deep_work_2" -> {
                            if (intention.techneProject.isNotBlank()) {
                                listOf("Phone on Do Not Distract", "Headphones on", "Work on: ${intention.techneProject}", "One concrete step forward")
                            } else alarm.tasks
                        }
                        "theory" -> {
                            if (intention.techneSkill.isNotBlank()) {
                                listOf("Breakfast + study", "Focus skill: ${intention.techneSkill}", "45 mins structured study")
                            } else if (intention.techneProject.isNotBlank()) {
                                listOf("Breakfast + study", "Research for: ${intention.techneProject}", "45 mins structured study")
                            } else alarm.tasks
                        }
                        "commit" -> {
                            if (intention.techneProject.isNotBlank()) {
                                listOf("git add . && git commit -m 'progress: ${intention.techneProject}'", "git push", "Close the laptop")
                            } else alarm.tasks
                        }
                        "primary_source" -> {
                            if (intention.historiaBook.isNotBlank() || intention.historiaTopic.isNotBlank()) {
                                val tasks = mutableListOf<String>()
                                if (intention.historiaBook.isNotBlank()) tasks.add("Read: ${intention.historiaBook}")
                                if (intention.historiaTopic.isNotBlank()) tasks.add("Research focus: ${intention.historiaTopic}")
                                tasks.add("Note one claim and one question")
                                tasks
                            } else alarm.tasks
                        }
                        "peripatetic" -> {
                            if (intention.historiaTopic.isNotBlank() || intention.historiaBook.isNotBlank()) {
                                listOf("Walk without phone", "Turn over: ${intention.historiaTopic.ifBlank { intention.historiaBook }}", "Return with one sentence")
                            } else alarm.tasks
                        }
                        "physical", "playground" -> {
                            if (intention.gymnosFocus.isNotBlank()) {
                                val tasks = alarm.tasks.toMutableList()
                                tasks.add(1, "Focus: ${intention.gymnosFocus}")
                                tasks
                            } else alarm.tasks
                        }
                        "scholar_compass", "digital_sunset", "evening_recovery" -> {
                            if (intention.sophiaTheme.isNotBlank()) {
                                val tasks = alarm.tasks.toMutableList()
                                // Inject into the first reflective-looking task
                                val idx = tasks.indexOfFirst { it.contains("Plan") || it.contains("Journaling") || it.contains("recovery") }
                                if (idx != -1) {
                                    tasks[idx] = "${tasks[idx]} — Theme: ${intention.sophiaTheme}"
                                } else {
                                    tasks.add(0, "Theme: ${intention.sophiaTheme}")
                                }
                                tasks
                            } else alarm.tasks
                        }
                        "grounding" -> {
                            if (intention.weekNote.isNotBlank()) {
                                alarm.tasks + "Week Intention: ${intention.weekNote}"
                            } else alarm.tasks
                        }
                        else -> alarm.tasks
                    }
                } else alarm.tasks

                alarm.copy(
                    time = alarm.time.plus(offset),
                    tasks = personalizedTasks
                )
            }
            pillar.copy(alarms = shiftedAlarms)
        }
    }
}
