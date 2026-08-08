package com.obrien.thecathedral.ui.skilltree

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import com.obrien.thecathedral.ui.theme.Bronze
import com.obrien.thecathedral.ui.theme.CathedralGold
import com.obrien.thecathedral.ui.theme.MonasteryBlack
import com.obrien.thecathedral.ui.theme.Parchment

@Composable
fun SkillTreeGraph(
    nodes: List<SkillNode>,
    edges: List<SkillEdge>
) {
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(MonasteryBlack)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    panOffset += dragAmount
                }
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // 1. Draw Edges (Constellation Lines)
        edges.forEach { edge ->
            val fromNode = nodes.find { it.id == edge.fromId }
            val toNode = nodes.find { it.id == edge.toId }
            
            if (fromNode != null && toNode != null) {
                val start = Offset(
                    (fromNode.position.x * canvasWidth) + panOffset.x,
                    (fromNode.position.y * canvasHeight) + panOffset.y
                )
                val end = Offset(
                    (toNode.position.x * canvasWidth) + panOffset.x,
                    (toNode.position.y * canvasHeight) + panOffset.y
                )

                drawLine(
                    color = if (fromNode.completed && toNode.unlocked) CathedralGold.copy(alpha = 0.6f) else Bronze.copy(alpha = 0.3f),
                    start = start,
                    end = end,
                    strokeWidth = 2f,
                    pathEffect = if (!fromNode.completed) PathEffect.dashPathEffect(floatArrayOf(10f, 10f)) else null
                )
            }
        }

        // 2. Draw Nodes (Glowing Orbs)
        nodes.forEach { node ->
            val center = Offset(
                (node.position.x * canvasWidth) + panOffset.x,
                (node.position.y * canvasHeight) + panOffset.y
            )
            
            val nodeColor = when {
                node.completed -> CathedralGold
                node.unlocked -> CathedralGold.copy(alpha = 0.5f)
                else -> Bronze.copy(alpha = 0.2f)
            }

            // Outer Glow
            if (node.unlocked) {
                drawCircle(
                    color = nodeColor.copy(alpha = 0.2f),
                    radius = 30f,
                    center = center
                )
            }
            
            // Core Orb
            drawCircle(
                color = nodeColor,
                radius = 16f,
                center = center
            )

            // Node Label
            val textLayoutResult = textMeasurer.measure(node.name)
            drawText(
                textLayoutResult = textLayoutResult,
                color = if (node.unlocked) Parchment else Parchment.copy(alpha = 0.4f),
                topLeft = Offset(
                    center.x - textLayoutResult.size.width / 2,
                    center.y + 24f
                )
            )
        }
    }
}
