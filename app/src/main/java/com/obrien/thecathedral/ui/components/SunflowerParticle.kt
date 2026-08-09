package com.obrien.thecathedral.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.obrien.thecathedral.ui.theme.CathedralGold
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SunflowerParticle(
    modifier: Modifier = Modifier,
    size: Float = 24f,
    color: Color = CathedralGold,
    drift: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sunflower")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatType.Restart
        ),
        label = "rotation"
    )

    val driftY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (drift) 15f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = SineEaseInOut),
            repeatMode = RepeatType.Reverse
        ),
        label = "drift"
    )

    Canvas(modifier = modifier.size(size.dp)) {
        val centerX = this.size.width / 2
        val centerY = this.size.height / 2 + driftY
        val radius = this.size.minDimension / 4

        // Draw center
        drawCircle(
            color = color.copy(alpha = 0.8f),
            radius = radius * 0.6f,
            center = Offset(centerX, centerY),
            style = Fill
        )

        // Draw petals
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
