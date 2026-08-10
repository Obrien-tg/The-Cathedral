package com.obrien.thecathedral.ui.skilltree

import androidx.compose.ui.geometry.Offset

/**
 * Visual representation of a skill node for the constellation canvas.
 * Positions are normalized (0.0 – 1.0) relative to the canvas.
 */
data class SkillNode(
    val id: String,
    val name: String,
    val position: Offset,
    val unlocked: Boolean = false,
    val completed: Boolean = false,
    val pillar: String,
    val progress: Float = 0f,
    val tier: Int = 0,
    val description: String = ""
)

data class SkillEdge(
    val fromId: String,
    val toId: String
)

/**
 * Canonical layout for the expanded 17-node formation path.
 * Designed as a rising constellation that still reads clearly when panned.
 */
object SkillTreeLayout {
    fun positionFor(nodeId: String): Offset = when (nodeId) {
        // Tier 0 – Foundation
        "1"  -> Offset(0.50f, 0.08f)   // Ignition

        // Tier 1 – First Formation
        "2"  -> Offset(0.22f, 0.22f)   // Deep Work I
        "3"  -> Offset(0.78f, 0.22f)   // The Archive
        "4"  -> Offset(0.50f, 0.28f)   // First Light

        // Tier 2 – Strengthening
        "5"  -> Offset(0.18f, 0.40f)   // Deep Work II
        "6"  -> Offset(0.82f, 0.40f)   // Living Sources
        "7"  -> Offset(0.50f, 0.46f)   // Physical Fortitude
        "8"  -> Offset(0.50f, 0.38f)   // Evening Vigil (slightly higher)

        // Tier 3 – Integration
        "9"  -> Offset(0.18f, 0.58f)   // The Forge Master
        "10" -> Offset(0.82f, 0.58f)   // The Chronicler
        "11" -> Offset(0.38f, 0.64f)   // Embodied Discipline
        "12" -> Offset(0.62f, 0.64f)   // Quiet Mind

        // Tier 4 – Synthesis
        "13" -> Offset(0.28f, 0.76f)   // Builder’s Hand
        "14" -> Offset(0.72f, 0.76f)   // Scholar’s Compass
        "15" -> Offset(0.50f, 0.82f)   // Guardian of the Day

        // Tier 5 – Capstone
        "16" -> Offset(0.50f, 0.90f)   // Master of Two Hands
        "17" -> Offset(0.50f, 0.97f)   // The Cathedral Complete

        else -> Offset(0.50f, 0.50f)
    }
}
