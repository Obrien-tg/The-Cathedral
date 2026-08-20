package com.obrien.thecathedral.model

import com.obrien.core.model.SkillNode
import com.obrien.core.model.SkillEdge

/**
 * The Cathedral Formation Path – expanded lifetime skill tree.
 */
object SkillTreeData {

    val nodes = listOf(
        // ── Tier 0 · Foundation ──────────────────────────────────────────
        SkillNode(
            id = "1",
            title = "Ignition",
            pillar = "AWAKENING",
            requiredCompletions = 7,
            requiredAlarmIds = listOf("ignition"),
            description = "Seven true beginnings. Sit up. No snooze. Own the first minute.",
            tier = 0
        ),

        // ── Tier 1 · First Formation ─────────────────────────────────────
        SkillNode(
            id = "2",
            title = "Deep Work I",
            pillar = "TECHNE",
            requiredCompletions = 12,
            requiredFocusSessions = 15,
            requiredAlarmIds = listOf("deep_work_1", "deep_work_2", "commit"),
            description = "Twelve blocks of focused craft and fifteen deliberate sessions.",
            tier = 1
        ),
        SkillNode(
            id = "3",
            title = "The Archive",
            pillar = "HISTORIA",
            requiredCompletions = 12,
            requiredJournalDays = 7,
            requiredAlarmIds = listOf("primary_source", "peripatetic"),
            description = "Engage the primary sources and leave a written trace for seven days.",
            tier = 1
        ),
        SkillNode(
            id = "4",
            title = "First Light",
            pillar = "GYMNOS",
            requiredCompletions = 10,
            requiredAlarmIds = listOf("physical"),
            description = "Ten days of elevated heart rate. The body begins to obey.",
            tier = 1
        ),

        // ── Tier 2 · Strengthening ───────────────────────────────────────
        SkillNode(
            id = "5",
            title = "Deep Work II",
            pillar = "TECHNE",
            requiredCompletions = 30,
            requiredFocusSessions = 45,
            requiredAlarmIds = listOf("deep_work_1", "deep_work_2", "commit"),
            description = "The forge grows hotter. Consistency over intensity.",
            tier = 2
        ),
        SkillNode(
            id = "6",
            title = "Living Sources",
            pillar = "HISTORIA",
            requiredCompletions = 30,
            requiredJournalDays = 21,
            requiredAlarmIds = listOf("primary_source", "peripatetic"),
            description = "Twenty-one days of recorded reflection on the texts that formed you.",
            tier = 2
        ),
        SkillNode(
            id = "7",
            title = "Physical Fortitude",
            pillar = "GYMNOS",
            requiredCompletions = 35,
            requiredAlarmIds = listOf("physical"),
            description = "The body becomes a reliable instrument.",
            tier = 2
        ),
        SkillNode(
            id = "8",
            title = "Evening Vigil",
            pillar = "SOPHIA",
            requiredCompletions = 21,
            requiredJournalDays = 14,
            requiredAlarmIds = listOf("digital_sunset", "scholar_compass"),
            description = "Close the day deliberately. Digital sunset and the Scholar’s Compass.",
            tier = 2
        ),

        // ── Tier 3 · Integration ─────────────────────────────────────────
        SkillNode(
            id = "9",
            title = "The Forge Master",
            pillar = "TECHNE",
            requiredCompletions = 60,
            requiredFocusSessions = 90,
            requiredAlarmIds = listOf("deep_work_1", "deep_work_2", "commit"),
            description = "Craft has become second nature. The hands know the work.",
            tier = 3
        ),
        SkillNode(
            id = "10",
            title = "The Chronicler",
            pillar = "HISTORIA",
            requiredCompletions = 60,
            requiredJournalDays = 50,
            requiredAlarmIds = listOf("primary_source", "peripatetic"),
            description = "A written record of formation. Memory made durable.",
            tier = 3
        ),
        SkillNode(
            id = "11",
            title = "Embodied Discipline",
            pillar = "GYMNOS",
            requiredCompletions = 70,
            requiredAlarmIds = listOf("physical"),
            description = "Physical practice is no longer optional. It is identity.",
            tier = 3
        ),
        SkillNode(
            id = "12",
            title = "Quiet Mind",
            pillar = "SOPHIA",
            requiredCompletions = 45,
            requiredJournalDays = 45,
            requiredAlarmIds = listOf("digital_sunset", "scholar_compass"),
            description = "Evening reflection has become a sanctuary, not a chore.",
            tier = 3
        ),

        // ── Tier 4 · Synthesis ───────────────────────────────────────────
        SkillNode(
            id = "13",
            title = "Builder’s Hand",
            pillar = "TECHNE",
            requiredCompletions = 90,
            requiredFocusSessions = 130,
            requiredAlarmIds = listOf("deep_work_1", "deep_work_2", "commit", "physical"),
            description = "Craft and body united. The hand that builds is also the body that endures.",
            tier = 4
        ),
        SkillNode(
            id = "14",
            title = "Scholar’s Compass",
            pillar = "SOPHIA",
            requiredCompletions = 80,
            requiredJournalDays = 70,
            requiredAlarmIds = listOf("primary_source", "digital_sunset", "scholar_compass"),
            description = "Study and wisdom fused. Direction is clear even in fog.",
            tier = 4
        ),
        SkillNode(
            id = "15",
            title = "Guardian of the Day",
            pillar = "SOPHIA",
            requiredCompletions = 120,
            requiredFocusSessions = 120,
            requiredJournalDays = 80,
            description = "The full day is held. Every pillar has been proven under load.",
            tier = 4
        ),

        // ── Tier 5 · Capstone ────────────────────────────────────────────
        SkillNode(
            id = "16",
            title = "Master of Two Hands",
            pillar = "SOPHIA",
            requiredCompletions = 180,
            requiredFocusSessions = 180,
            requiredJournalDays = 120,
            description = "I am one of many. But I am the master of my own two hands.",
            tier = 5
        ),
        SkillNode(
            id = "17",
            title = "The Cathedral Complete",
            pillar = "SOPHIA",
            requiredCompletions = 250,
            requiredFocusSessions = 250,
            requiredJournalDays = 180,
            description = "The structure stands. Formation is no longer a project — it is the life.",
            tier = 5
        )
    )

    val edges = listOf(
        // Tier 0 → Tier 1
        SkillEdge("1", "2"),
        SkillEdge("1", "3"),
        SkillEdge("1", "4"),

        // Tier 1 → Tier 2
        SkillEdge("2", "5"),
        SkillEdge("3", "6"),
        SkillEdge("4", "7"),
        SkillEdge("1", "8"),

        // Tier 2 → Tier 3
        SkillEdge("5", "9"),
        SkillEdge("6", "10"),
        SkillEdge("7", "11"),
        SkillEdge("8", "12"),

        // Tier 3 → Tier 4 (synthesis requires multiple parents)
        SkillEdge("9", "13"),
        SkillEdge("11", "13"),
        SkillEdge("10", "14"),
        SkillEdge("12", "14"),

        // Guardian of the Day demands broad excellence
        SkillEdge("9", "15"),
        SkillEdge("10", "15"),
        SkillEdge("11", "15"),
        SkillEdge("12", "15"),
        SkillEdge("13", "15"),
        SkillEdge("14", "15"),

        // Capstone
        SkillEdge("15", "16"),
        SkillEdge("16", "17")
    )

    /** Convenience lookup */
    fun nodeById(id: String): SkillNode? = nodes.find { it.id == id }

    /** All nodes that must be completed before this one unlocks */
    fun parentsOf(nodeId: String): List<String> =
        edges.filter { it.to == nodeId }.map { it.from }
}
