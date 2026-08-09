package com.obrien.thecathedral.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.thecathedral.model.FocusQuotes
import com.obrien.thecathedral.ui.theme.CathedralGold
import com.obrien.thecathedral.ui.theme.MonasteryBlack
import com.obrien.thecathedral.ui.theme.Parchment
import com.obrien.thecathedral.ui.theme.RitualMiss
import com.obrien.thecathedral.ui.theme.TheCathedralTheme
import com.obrien.thecathedral.viewmodel.ScheduleViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusModeScreen(
    viewModel: ScheduleViewModel,
    onBack: () -> Unit = {}
) {
    val timeRemaining by viewModel.focusTimeRemaining.collectAsState()
    val isRunning by viewModel.focusIsRunning.collectAsState()
    val sessionCount by viewModel.focusSessionCount.collectAsState()
    
    var quoteIndex by remember { mutableIntStateOf(0) }
    var showCompletion by remember { mutableStateOf(false) }

    val quotes = FocusQuotes.all

    // Quote rotation
    LaunchedEffect(isRunning, timeRemaining) {
        if (isRunning && timeRemaining % 30 == 0) {
            quoteIndex = (quoteIndex + 1) % quotes.size
        }
    }

    // Completion celebration
    LaunchedEffect(timeRemaining) {
        if (timeRemaining == 0 && !isRunning && sessionCount > 0) {
            showCompletion = true
            delay(2000)
            showCompletion = false
        }
    }

    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val progress = 1f - (timeRemaining / (25f * 60f))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "THE FORGE",
                        style = MaterialTheme.typography.titleMedium,
                        letterSpacing = 3.sp,
                        color = CathedralGold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CathedralGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MonasteryBlack)
            )
        },
        containerColor = MonasteryBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (showCompletion) "RITUAL SEALED" else "SESSION ${sessionCount + 1}",
                style = MaterialTheme.typography.labelMedium,
                color = CathedralGold.copy(alpha = 0.6f),
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = CathedralGold,
                    trackColor = CathedralGold.copy(alpha = 0.1f),
                    strokeWidth = 4.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        style = MaterialTheme.typography.displayLarge,
                        color = if (timeRemaining <= 60 && timeRemaining > 0) RitualMiss else Parchment,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Light
                    )
                    Text(
                        text = if (isRunning) "FORGING..." else if (timeRemaining == 0) "COMPLETE" else "READY",
                        style = MaterialTheme.typography.labelSmall,
                        color = CathedralGold.copy(alpha = 0.7f),
                        letterSpacing = 3.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(
                targetState = quoteIndex,
                label = "quote"
            ) { idx ->
                val quote = quotes[idx]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = """"${quote.text}"""",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Parchment.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 26.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "— ${quote.author}, ${quote.source}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CathedralGold.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.resetFocusTimer() },
                    modifier = Modifier
                        .size(56.dp)
                        .background(CathedralGold.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = CathedralGold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = { if (isRunning) viewModel.pauseFocusTimer() else viewModel.startFocusTimer() },
                    modifier = Modifier
                        .size(72.dp)
                        .background(CathedralGold, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        tint = MonasteryBlack,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.setFocusBreak() },
                    modifier = Modifier
                        .size(56.dp)
                        .background(CathedralGold.copy(alpha = 0.1f), CircleShape)
                ) {
                    Text(
                        text = "5m",
                        color = CathedralGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "The 5-Minute Rescue: commit to just 5 minutes. You will likely continue.",
                style = MaterialTheme.typography.bodySmall,
                color = Parchment.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        }
    }
}
