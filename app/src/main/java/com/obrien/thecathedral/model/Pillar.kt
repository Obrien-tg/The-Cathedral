package com.obrien.thecathedral.model

import java.time.LocalTime

data class Pillar(
    val id: String,
    val name: String,
    val timeRange: String,
    val alarms: List<Alarm>,
    val description: String = ""
)

data class Alarm(
    val id: String,
    val time: LocalTime,
    val name: String,
    val tasks: List<String>,
    val status: PillarStatus = PillarStatus.PENDING
)

enum class PillarStatus {
    PENDING,
    ACTIVE,
    COMPLETE,
    MISSED,
    SKIPPED
}
