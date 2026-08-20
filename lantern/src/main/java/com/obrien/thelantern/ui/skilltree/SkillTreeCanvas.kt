package com.obrien.thelantern.ui.skilltree

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import com.obrien.thelantern.ui.theme.LanternBlue
import com.obrien.thelantern.ui.theme.LanternGold
import com.obrien.thelantern.ui.theme.LanternNight
import com.obrien.thelantern.ui.theme.LanternText
import kotlin.math.hypot

@Composable
fun SkillTreeGraph(
    nodes: List<SkillNode>,
    edges: List<SkillEdge>,
    onNodeClick: (String) -> Unit = {}
) {
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }
    val textMeasurer = rememberTextMeasurer()

    // Keep latest transform values readable inside pointerInput
    val currentPan by rememberUpdatedState(panOffset)
    val currentScale by rememberUpdatedState(scale)
    val currentNodes by rememberUpdatedState(nodes)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(LanternNight)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.55f, 2.2f)
                    panOffset += pan
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val pivot = Offset(canvasWidth / 2f, canvasHeight / 2f)

                    // Inverse of the withTransform used when drawing
                    val unscaled = (tapOffset - currentPan - pivot) / currentScale + pivot

                    // Hit-test nodes (generous radius for finger taps)
                    val hit = currentNodes.minByOrNull { node ->
                        val center = Offset(
                            node.position.x * canvasWidth,
                            node.position.y * canvasHeight
                        )
                        hypot(
                            (unscaled.x - center.x).toDouble(),
                            (unscaled.y - center.y).toDouble()
                        )
                    }

                    if (hit != null) {
                        val center = Offset(
                            hit.position.x * canvasWidth,
                            hit.position.y * canvasHeight
                        )
                        val dist = hypot(
                            (unscaled.x - center.x).toDouble(),
                            (unscaled.y - center.y).toDouble()
                        )
                        // ~48dp-equivalent hit radius in canvas space
                        if (dist < 48.0 / currentScale) {
                            onNodeClick(hit.id)
                        }
                    }
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
                        color = if (active) LanternGold.copy(alpha = 0.65f)
                        else LanternBlue.copy(alpha = 0.28f),
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
                    node.completed -> LanternGold
                    node.unlocked -> LanternGold.copy(alpha = 0.55f)
                    else -> LanternBlue.copy(alpha = 0.22f)
                }

                // Soft outer glow
                if (node.unlocked || node.completed) {
                    drawCircle(
                        color = nodeColor.copy(alpha = 0.18f),
                        radius = if (node.completed) 34f else 28f,
                        center = center
                    )
                }

                // Core orb – larger for higher tiers
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

                // Progress ring
                if (node.unlocked && !node.completed && node.progress > 0f) {
                    drawArc(
                        color = LanternGold,
                        startAngle = -90f,
                        sweepAngle = 360f * node.progress,
                        useCenter = false,
                        topLeft = Offset(center.x - 22f, center.y - 22f),
                        size = Size(44f, 44f),
                        style = Stroke(width = 3.2f)
                    )
                }

                // Double ring for synthesis / capstone
                if (node.tier >= 4 && (node.unlocked || node.completed)) {
                    drawCircle(
                        color = LanternGold.copy(alpha = 0.45f),
                        radius = coreRadius + 8f,
                        center = center,
                        style = Stroke(width = 1.6f)
                    )
                }

                // Label
                val labelStyle = TextStyle(
                    fontSize = if (node.tier >= 4) 11.sp else 10.sp,
                    fontWeight = if (node.completed) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (node.unlocked) LanternText else LanternText.copy(alpha = 0.38f)
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
