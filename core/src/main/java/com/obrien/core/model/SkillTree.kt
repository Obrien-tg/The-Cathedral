package com.obrien.core.model

data class SkillNode(
    val id: String,
    val title: String,
    val pillar: String,
    val requiredCompletions: Int = 1,
    val requiredFocusSessions: Int = 0,
    val requiredJournalDays: Int = 0,
    val requiredAlarmIds: List<String> = emptyList(),
    val description: String = "",
    val tier: Int = 0
)

data class SkillEdge(
    val from: String,
    val to: String
)

data class SkillProgress(
    val nodeId: String,
    val unlocked: Boolean,
    val completed: Boolean,
    val progress: Float
)
