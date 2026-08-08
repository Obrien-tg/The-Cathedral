package com.example.thecathedral.ui.skilltree

import androidx.compose.ui.geometry.Offset

data class SkillNode(
    val id: String,
    val name: String,
    val position: Offset, // Normalized coordinates (0.0 to 1.0)
    val unlocked: Boolean = false,
    val completed: Boolean = false,
    val pillar: String // TECHNE, HISTORIA, etc.
)

data class SkillEdge(
    val fromId: String,
    val toId: String
)
