package com.obrien.core.focus

data class FocusSession(
    val kind: FocusKind,
    val target: String = "",
    val durationMinutes: Int,
    val startTimeMillis: Long = System.currentTimeMillis()
)
