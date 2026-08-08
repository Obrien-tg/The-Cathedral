package com.obrien.thecathedral.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.thecathedral.data.ScheduleData
import com.obrien.thecathedral.ui.theme.CathedralGold
import com.obrien.thecathedral.ui.theme.MonasteryBlack
import com.obrien.thecathedral.ui.theme.Parchment
import com.obrien.thecathedral.ui.theme.TheCathedralTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit = {}
) {
    var startAnimation by remember { mutableStateOf(false) }

    val iconScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.6f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "iconScale"
    )

    val iconAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 100),
        label = "iconAlpha"
    )

    val titleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 900, delayMillis = 600),
        label = "titleAlpha"
    )

    val mantraAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 900, delayMillis = 1100),
        label = "mantraAlpha"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 0.15f else 0f,
        animationSpec = tween(durationMillis = 1500, delayMillis = 200),
        label = "glowAlpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        startAnimation = true
        delay(2800)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MonasteryBlack),
        contentAlignment = Alignment.Center
    ) {
        // Subtle radial glow behind the icon
        Box(
            modifier = Modifier
                .size(280.dp)
                .alpha(glowAlpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CathedralGold.copy(alpha = 0.3f),
                            CathedralGold.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    ),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(iconScale)
                    .alpha(iconAlpha),
                contentAlignment = Alignment.Center
            ) {
                RoseWindowIcon(
                    modifier = Modifier.fillMaxSize(),
                    color = CathedralGold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "THE CATHEDRAL",
                style = MaterialTheme.typography.headlineMedium,
                color = CathedralGold,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(titleAlpha)
            )

            Text(
                text = "CODEX",
                style = MaterialTheme.typography.titleSmall,
                color = CathedralGold.copy(alpha = 0.6f),
                fontWeight = FontWeight.Light,
                letterSpacing = 8.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(titleAlpha)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = """"${ScheduleData.MANTRA}"""",
                style = MaterialTheme.typography.bodyMedium,
                color = Parchment.copy(alpha = 0.7f),
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier
                    .alpha(mantraAlpha)
                    .padding(horizontal = 32.dp)
            )
        }

        // Bottom sigil
        Text(
            text = "TG",
            style = MaterialTheme.typography.labelSmall,
            color = CathedralGold.copy(alpha = 0.2f),
            letterSpacing = 3.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .alpha(mantraAlpha)
        )
    }
}

@Composable
fun RoseWindowIcon(
    modifier: Modifier = Modifier,
    color: Color = CathedralGold
) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val outerRadius = size.minDimension / 2 - 4f
        val innerRadius = outerRadius * 0.75f
        val coreRadius = outerRadius * 0.22f

        drawCircle(
            color = color,
            radius = outerRadius,
            center = androidx.compose.ui.geometry.Offset(centerX, centerY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
        )

        drawCircle(
            color = color,
            radius = innerRadius,
            center = androidx.compose.ui.geometry.Offset(centerX, centerY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
        )

        for (i in 0 until 8) {
            val angle = Math.PI * i / 4
            val x1 = centerX + innerRadius * kotlin.math.cos(angle).toFloat()
            val y1 = centerY + innerRadius * kotlin.math.sin(angle).toFloat()
            val x2 = centerX + outerRadius * kotlin.math.cos(angle).toFloat()
            val y2 = centerY + outerRadius * kotlin.math.sin(angle).toFloat()
            drawLine(
                color = color,
                start = androidx.compose.ui.geometry.Offset(x1, y1),
                end = androidx.compose.ui.geometry.Offset(x2, y2),
                strokeWidth = 2f
            )
        }

        drawCircle(
            color = color,
            radius = coreRadius,
            center = androidx.compose.ui.geometry.Offset(centerX, centerY)
        )

        val flamePath = Path().apply {
            moveTo(centerX, centerY - coreRadius * 0.5f)
            lineTo(centerX + coreRadius * 0.4f, centerY + coreRadius * 0.2f)
            lineTo(centerX, centerY + coreRadius * 0.6f)
            lineTo(centerX - coreRadius * 0.4f, centerY + coreRadius * 0.2f)
            close()
        }
        drawPath(
            path = flamePath,
            color = MonasteryBlack
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    TheCathedralTheme(darkTheme = true) {
        SplashScreen()
    }
}
