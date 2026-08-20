package com.obrien.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LanternParticle(
    modifier: Modifier = Modifier,
    size: Float = 24f,
    color: Color = Color(0xFFE6B800),
    drift: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lantern")
    
    val flicker by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flicker"
    )

    val driftY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (drift) 10f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = SineEaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )

    Canvas(modifier = modifier.size(size.dp)) {
        val centerX = this.size.width / 2
        val centerY = this.size.height / 2 + driftY
        val flameWidth = this.size.width * 0.4f * flicker
        val flameHeight = this.size.height * 0.7f * flicker

        // Draw outer glow
        drawCircle(
            color = color.copy(alpha = 0.1f),
            radius = flameHeight * 1.2f,
            center = Offset(centerX, centerY)
        )

        // Draw flame path
        val path = Path().apply {
            moveTo(centerX, centerY - flameHeight / 2)
            quadraticBezierTo(
                centerX + flameWidth / 2, centerY,
                centerX, centerY + flameHeight / 2
            )
            quadraticBezierTo(
                centerX - flameWidth / 2, centerY,
                centerX, centerY - flameHeight / 2
            )
        }

        drawPath(
            path = path,
            color = color.copy(alpha = 0.8f),
            style = Fill
        )

        val innerPath = Path().apply {
            moveTo(centerX, centerY - flameHeight / 4)
            quadraticBezierTo(
                centerX + flameWidth / 4, centerY,
                centerX, centerY + flameHeight / 4
            )
            quadraticBezierTo(
                centerX - flameWidth / 4, centerY,
                centerX, centerY - flameHeight / 4
            )
        }

        drawPath(
            path = innerPath,
            color = Color(0xFFE8A0BF).copy(alpha = 0.6f),
            style = Fill
        )
    }
}

@Composable
fun SunflowerParticle(
    modifier: Modifier = Modifier,
    size: Float = 24f,
    color: Color = Color(0xFFD4AF37),
    drift: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sunflower")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val driftY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (drift) 15f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = SineEaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )

    Canvas(modifier = modifier.size(size.dp)) {
        val centerX = this.size.width / 2
        val centerY = this.size.height / 2 + driftY
        val radius = this.size.minDimension / 4

        drawCircle(
            color = color.copy(alpha = 0.8f),
            radius = radius * 0.6f,
            center = Offset(centerX, centerY),
            style = Fill
        )

        val petalCount = 8
        for (i in 0 until petalCount) {
            val angle = Math.toRadians((rotation + (i * 360 / petalCount)).toDouble())
            val petalRadius = radius * 1.2f
            val px = centerX + cos(angle).toFloat() * petalRadius
            val py = centerY + sin(angle).toFloat() * petalRadius
            
            drawCircle(
                color = color.copy(alpha = 0.4f),
                radius = radius * 0.4f,
                center = Offset(px, py),
                style = Fill
            )
            
            drawCircle(
                color = color.copy(alpha = 0.6f),
                radius = radius * 0.4f,
                center = Offset(px, py),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

private val SineEaseInOut = Easing { fraction ->
    ((1 - cos(Math.PI * fraction)) / 2).toFloat()
}
