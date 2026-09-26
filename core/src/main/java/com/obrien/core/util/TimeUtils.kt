package com.obrien.core.util

import com.obrien.core.model.Pillar
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun Pillar.isActiveAt(time: LocalTime): Boolean {
    return try {
        val parts = timeRange.split(" - ")
        val start = LocalTime.parse(parts[0], DateTimeFormatter.ofPattern("HH:mm"))
        val end = LocalTime.parse(parts[1], DateTimeFormatter.ofPattern("HH:mm"))
        
        if (start.isBefore(end)) {
            !time.isBefore(start) && time.isBefore(end)
        } else {
            // Over midnight
            !time.isBefore(start) || time.isBefore(end)
        }
    } catch (e: Exception) {
        false
    }
}
