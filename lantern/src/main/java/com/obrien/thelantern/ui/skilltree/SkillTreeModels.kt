package com.obrien.thelantern.ui.skilltree

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
        "first_light"    -> Offset(0.50f, 0.10f)
        "present"        -> Offset(0.28f, 0.22f)
        "study_seed"     -> Offset(0.50f, 0.28f)
        "reader"         -> Offset(0.72f, 0.22f)
        "hard_courage"   -> Offset(0.20f, 0.42f)
        "body_steady"    -> Offset(0.50f, 0.46f)
        "clean_evening"  -> Offset(0.80f, 0.42f)
        "full_week"      -> Offset(0.28f, 0.62f)
        "curious"        -> Offset(0.50f, 0.65f)
        "kind_strength"  -> Offset(0.72f, 0.62f)
        "own_word"       -> Offset(0.50f, 0.82f)
        "the_lantern"    -> Offset(0.50f, 0.95f)
        else -> Offset(0.50f, 0.50f)
    }
}
