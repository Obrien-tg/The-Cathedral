package com.obrien.thecathedral.ui.skilltree

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
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
    var scale by remember { mutableFloatStateOf(1f) }
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(MonasteryBlack)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.55f, 2.2f)
                    panOffset += pan
                }
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        withTransform({
            translate(panOffset.x, panOffset.y)
            scale(scale, scale, pivot = Offset(canvasWidth / 2f, canvasHeight / 2f))
        }) {
            // 1. Edges – constellation lines
            edges.forEach { edge ->
                val fromNode = nodes.find { it.id == edge.fromId }
                val toNode = nodes.find { it.id == edge.toId }

                if (fromNode != null && toNode != null) {
                    val start = Offset(
                        fromNode.position.x * canvasWidth,
                        fromNode.position.y * canvasHeight
                    )
                    val end = Offset(
                        toNode.position.x * canvasWidth,
                        toNode.position.y * canvasHeight
                    )

                    val active = fromNode.completed && toNode.unlocked
                    drawLine(
                        color = if (active) CathedralGold.copy(alpha = 0.65f)
                        else Bronze.copy(alpha = 0.28f),
                        start = start,
                        end = end,
                        strokeWidth = if (active) 2.5f else 1.8f,
                        pathEffect = if (!fromNode.completed)
                            PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
                        else null
                    )
                }
            }

            // 2. Nodes – glowing orbs
            nodes.forEach { node ->
                val center = Offset(
                    node.position.x * canvasWidth,
                    node.position.y * canvasHeight
                )

                val nodeColor = when {
                    node.completed -> CathedralGold
                    node.unlocked -> CathedralGold.copy(alpha = 0.55f)
                    else -> Bronze.copy(alpha = 0.22f)
                }

                // Soft outer glow for unlocked / completed nodes
                if (node.unlocked || node.completed) {
                    drawCircle(
                        color = nodeColor.copy(alpha = 0.18f),
                        radius = if (node.completed) 34f else 28f,
                        center = center
                    )
                }

                // Core orb – slightly larger for higher tiers
                val coreRadius = when {
                    node.tier >= 5 -> 19f
                    node.tier >= 4 -> 17f
                    else -> 15f
                }
                drawCircle(
                    color = nodeColor,
                    radius = coreRadius,
                    center = center
                )

                // Progress ring for in-progress unlocked nodes
                if (node.unlocked && !node.completed && node.progress > 0f) {
                    drawArc(
                        color = CathedralGold,
                        startAngle = -90f,
                        sweepAngle = 360f * node.progress,
                        useCenter = false,
                        topLeft = Offset(center.x - 22f, center.y - 22f),
                        size = Size(44f, 44f),
                        style = Stroke(width = 3.2f)
                    )
                }

                // Double ring for synthesis / capstone nodes
                if (node.tier >= 4 && (node.unlocked || node.completed)) {
                    drawCircle(
                        color = CathedralGold.copy(alpha = 0.45f),
                        radius = coreRadius + 8f,
                        center = center,
                        style = Stroke(width = 1.6f)
                    )
                }

                // Label
                val labelStyle = TextStyle(
                    fontSize = if (node.tier >= 4) 11.sp else 10.sp,
                    fontWeight = if (node.completed) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (node.unlocked) Parchment else Parchment.copy(alpha = 0.38f)
                )
                val textLayout = textMeasurer.measure(node.name, style = labelStyle)
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        center.x - textLayout.size.width / 2f,
                        center.y + coreRadius + 10f
                    )
                )
            }
        }
    }
}
