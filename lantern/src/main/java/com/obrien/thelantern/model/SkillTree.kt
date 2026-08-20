package com.obrien.thelantern.model

import com.obrien.core.model.SkillNode
import com.obrien.core.model.SkillEdge

/**
 * The Lantern Formation Path – Grade 7 lifetime skill tree.
 */
object SkillTreeData {

    val nodes = listOf(
        SkillNode(
            id = "first_light",
            title = "First Light",
            pillar = "MORNING",
            requiredCompletions = 5,
            requiredAlarmIds = listOf("wake", "ready"),
            description = "Five true starts. Up on time and ready for the day.",
            tier = 0
        ),
        SkillNode(
            id = "present",
            title = "Present",
            pillar = "SCHOOL",
            requiredCompletions = 10,
            requiredAlarmIds = listOf("present"),
            description = "Showing up matters. Ten marks of presence in class.",
            tier = 1
        ),
        SkillNode(
            id = "study_seed",
            title = "Study Seed",
            pillar = "STUDY",
            requiredCompletions = 8,
            requiredAlarmIds = listOf("deep_study_1", "deep_study_2"),
            description = "Beginning the forge. Eight blocks of focused study.",
            tier = 1
        ),
        SkillNode(
            id = "reader",
            title = "Reader",
            pillar = "EVENING",
            requiredCompletions = 7,
            requiredAlarmIds = listOf("read"),
            description = "A quiet mind. Seven evenings of reading.",
            tier = 1
        ),
        SkillNode(
            id = "hard_courage",
            title = "Hard Subject Courage",
            pillar = "STUDY",
            requiredCompletions = 15,
            requiredAlarmIds = listOf("deep_study_1"),
            description = "Tackling the hard things first. Fifteen deep study blocks.",
            tier = 2
        ),
        SkillNode(
            id = "body_steady",
            title = "Body Steady",
            pillar = "BODY",
            requiredCompletions = 12,
            requiredAlarmIds = listOf("move"),
            description = "Strength and health. Twelve days of movement.",
            tier = 2
        ),
        SkillNode(
            id = "clean_evening",
            title = "Clean Evenings",
            pillar = "EVENING",
            requiredCompletions = 14,
            requiredAlarmIds = listOf("screens_down", "sleep"),
            description = "Ending well. Fourteen nights of screens down and sleep on time.",
            tier = 2
        ),
        SkillNode(
            id = "full_week",
            title = "Full School Week",
            pillar = "SCHOOL",
            requiredCompletions = 20,
            requiredAlarmIds = listOf("present", "learned", "bag_tomorrow"),
            description = "The balanced week. Consistent school and preparation.",
            tier = 3
        ),
        SkillNode(
            id = "curious",
            title = "Curious Mind",
            pillar = "SCHOOL",
            requiredCompletions = 15,
            requiredAlarmIds = listOf("learned", "read"),
            description = "Seeking knowledge. Fifteen marks of learning and reading.",
            tier = 3
        ),
        SkillNode(
            id = "kind_strength",
            title = "Kind Strength",
            pillar = "BODY",
            requiredCompletions = 15,
            requiredAlarmIds = listOf("courage_kind", "belong"),
            description = "Character in action. Fifteen moments of kindness and belonging.",
            tier = 3
        ),
        SkillNode(
            id = "own_word",
            title = "My Own Word",
            pillar = "EVENING",
            requiredCompletions = 30,
            requiredAlarmIds = listOf("deep_study_1", "screens_down", "sleep"),
            description = "Keeping promises to yourself. Sustained study and rest.",
            tier = 4
        ),
        SkillNode(
            id = "the_lantern",
            title = "The Lantern",
            pillar = "EVENING",
            requiredCompletions = 50,
            requiredJournalDays = 30,
            description = "The capstone of balance. Lighting the way to your own greatness.",
            tier = 5
        )
    )

    val edges = listOf(
        SkillEdge("first_light", "present"),
        SkillEdge("first_light", "study_seed"),
        SkillEdge("first_light", "reader"),
        SkillEdge("study_seed", "hard_courage"),
        SkillEdge("first_light", "body_steady"),
        SkillEdge("first_light", "clean_evening"),
        SkillEdge("present", "full_week"),
        SkillEdge("study_seed", "full_week"),
        SkillEdge("reader", "curious"),
        SkillEdge("present", "curious"),
        SkillEdge("present", "kind_strength"),
        SkillEdge("body_steady", "kind_strength"),
        SkillEdge("hard_courage", "own_word"),
        SkillEdge("clean_evening", "own_word"),
        SkillEdge("full_week", "own_word"),
        SkillEdge("own_word", "the_lantern"),
        SkillEdge("curious", "the_lantern"),
        SkillEdge("kind_strength", "the_lantern")
    )

    /** Convenience lookup */
    fun nodeById(id: String): SkillNode? = nodes.find { it.id == id }

    /** All nodes that must be completed before this one unlocks */
    fun parentsOf(nodeId: String): List<String> =
        edges.filter { it.to == nodeId }.map { it.from }
}
