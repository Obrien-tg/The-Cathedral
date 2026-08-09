package com.obrien.thecathedral.model

data class SkillNode(
    val id: String,
    val title: String,
    val pillar: String,
    val requiredCompletions: Int = 1,
    val requiredFocusSessions: Int = 0,
    val requiredJournalDays: Int = 0,
    val unlocks: List<String> = emptyList()
)

data class SkillEdge(
    val from: String,
    val to: String
)

data class SkillProgress(
    val nodeId: String,
    val unlocked: Boolean,
    val completed: Boolean,
    val progress: Float // 0f..1f
)

object SkillTreeData {
    val nodes = listOf(
        SkillNode(
            id = "1",
            title = "Ignition",
            pillar = "AWAKENING",
            requiredCompletions = 1
        ),
        SkillNode(
            id = "2",
            title = "Deep Work I",
            pillar = "TECHNE",
            requiredCompletions = 3,
            requiredFocusSessions = 5
        ),
        SkillNode(
            id = "3",
            title = "The Archive",
            pillar = "HISTORIA",
            requiredCompletions = 3,
            requiredJournalDays = 3
        ),
        SkillNode(
            id = "4",
            title = "Physical Fortitude",
            pillar = "GYMNOS",
            requiredCompletions = 5
        ),
        SkillNode(
            id = "5",
            title = "Scholar's Compass",
            pillar = "SOPHIA",
            requiredCompletions = 5,
            requiredJournalDays = 7
        )
    )

    val edges = listOf(
        SkillEdge("1", "2"),
        SkillEdge("1", "3"),
        SkillEdge("2", "4"),
        SkillEdge("3", "4"),
        SkillEdge("4", "5")
    )
}
