package com.obrien.thelantern.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.thelantern.ui.theme.LanternGold
import com.obrien.thelantern.ui.theme.LanternNight
import com.obrien.thelantern.ui.theme.TheLanternTheme
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun FocusScreen(
    durationMinutes: Int = 90,
    onEndSession: () -> Unit = {}
) {
    var timeLeftMillis by remember { mutableStateOf(TimeUnit.MINUTES.toMillis(durationMinutes.toLong())) }
    val totalTimeMillis = TimeUnit.MINUTES.toMillis(durationMinutes.toLong())
    
    val quotes = listOf(
        "Clarity over chaos. One purpose.",
        "Today I will build. Tomorrow I will build again.",
        "Master of my own two hands.",
        "The discipline of today is the freedom of tomorrow.",
        "Work in the deep. Leave the surface for others."
    )
    
    val currentQuoteIndex by remember { derivedStateOf { ((totalTimeMillis - timeLeftMillis) / 300000 % quotes.size).toInt() } }

    LaunchedEffect(key1 = timeLeftMillis) {
        if (timeLeftMillis > 0) {
            delay(1000L)
            timeLeftMillis -= 1000L
        }
    }

    Scaffold(
        containerColor = LanternNight
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title
            Text(
                text = "DEEP WORK RITUAL",
                style = MaterialTheme.typography.labelLarge,
                color = LanternGold,
                letterSpacing = 4.sp
            )

            // Timer Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(300.dp)
            ) {
                val progress by animateFloatAsState(
                    targetValue = timeLeftMillis.toFloat() / totalTimeMillis,
                    label = "TimerProgress"
                )
                
                Canvas(modifier = Modifier.size(280.dp)) {
                    drawArc(
                        color = LanternGold.copy(alpha = 0.1f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = LanternGold,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftMillis)
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis) % 60
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        style = MaterialTheme.typography.displayLarge,
                        color = LanternGold,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Light
                    )
                    Text(
                        text = "REMAINING",
                        style = MaterialTheme.typography.labelSmall,
                        color = LanternGold.copy(alpha = 0.5f),
                        letterSpacing = 2.sp
                    )
                }
            }

            // Quote Section
            Text(
                text = "\"${quotes[currentQuoteIndex]}\"",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // End Session Button
            var showConfirmDialog by remember { mutableStateOf(false) }
            
            OutlinedButton(
                onClick = { showConfirmDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LanternGold),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(width = 1.dp)
            ) {
                Text("END SESSION", letterSpacing = 2.sp)
            }

            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    containerColor = LanternNight,
                    titleContentColor = LanternGold,
                    textContentColor = MaterialTheme.colorScheme.onBackground,
                    title = { Text("BREAK THE RITUAL?", fontFamily = FontFamily.Serif) },
                    text = { Text("Ending now will interrupt your progress. Are you sure you wish to leave the deep?") },
                    confirmButton = {
                        TextButton(onClick = onEndSession) {
                            Text("I AM DONE", color = LanternGold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text("STAY", color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun FocusScreenPreview() {
    TheLanternTheme(darkTheme = true) {
        FocusScreen(durationMinutes = 90)
    }
}
