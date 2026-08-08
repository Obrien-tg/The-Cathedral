package com.example.thecathedral.util

import com.example.thecathedral.model.Alarm
import com.example.thecathedral.model.Pillar
import com.example.thecathedral.model.PillarStatus
import java.time.LocalTime

fun Pillar.isActiveAt(time: LocalTime): Boolean {
    val (start, end) = parseTimeRange() ?: return false
    return !time.isBefore(start) && time.isBefore(end)
}

fun Pillar.parseTimeRange(): Pair<LocalTime, LocalTime>? {
    val regex = """(\d{1,2}):(\d{2})\s*-\s*(\d{1,2}):(\d{2})""".toRegex()
    val match = regex.find(timeRange) ?: return null
    val (h1, m1, h2, m2) = match.destructured
    return try {
        LocalTime.of(h1.toInt(), m1.toInt()) to LocalTime.of(h2.toInt(), m2.toInt())
    } catch (_: Exception) {
        null
    }
}

fun Alarm.computeStatus(
    completed: Boolean,
    currentTime: LocalTime,
    activeWindowMinutes: Long = 15
): PillarStatus {
    return when {
        completed -> PillarStatus.COMPLETE
        currentTime.isAfter(time.plusMinutes(activeWindowMinutes)) -> PillarStatus.MISSED
        !currentTime.isBefore(time) && currentTime.isBefore(time.plusMinutes(activeWindowMinutes)) -> PillarStatus.ACTIVE
        else -> PillarStatus.PENDING
    }
}
