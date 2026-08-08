package com.obrien.thecathedral.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

// 1. Glassmorphism Modifier for Cards
fun Modifier.glassCard(): Modifier = composed {
    this
        .clip(RoundedCornerShape(16.dp))
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.07f),
                    Color.White.copy(alpha = 0.02f)
                )
            )
        )
        .border(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    CathedralGold.copy(alpha = 0.4f),
                    Bronze.copy(alpha = 0.1f)
                )
            ),
            shape = RoundedCornerShape(16.dp)
        )
}

// 2. Ambient Dust Motes (Monastery Atmosphere)
@Composable
fun AmbientDust(modifier: Modifier = Modifier) {
    val particles = remember {
        List(40) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2f + 1f,
                speed = Random.nextFloat() * 0.5f + 0.2f,
                phase = Random.nextFloat() * 6.28f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "dust")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f, // 2 PI
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            val yPos = (p.y - (time * p.speed) % 1f).let { if (it < 0) it + 1f else it }
            val xOffset = sin(time + p.phase) * 0.02f
            
            drawCircle(
                color = CathedralGold.copy(alpha = 0.15f),
                radius = p.size * 2,
                center = Offset(
                    x = (p.x + xOffset) * size.width,
                    y = yPos * size.height
                ),
                style = Fill
            )
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val phase: Float
)
