package com.obrien.thelantern.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.core.focus.FocusKind
import com.obrien.thelantern.ui.theme.*
import com.obrien.thelantern.viewmodel.FocusViewModel
import kotlinx.coroutines.delay

@Composable
fun FocusModeScreen(
    viewModel: FocusViewModel,
    onBack: () -> Unit = {}
) {
    val isRunning by viewModel.isRunning.collectAsState()
    
    if (isRunning) {
        FocusSessionContent(viewModel, onBack)
    } else {
        FocusSetupContent(viewModel, onBack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusSetupContent(
    viewModel: FocusViewModel,
    onBack: () -> Unit
) {
    var selectedKind by remember { mutableStateOf<FocusKind?>(null) }
    val suggestedPrompt by viewModel.suggestedPrompt.collectAsState()
    var customTarget by remember(suggestedPrompt) { mutableStateOf(suggestedPrompt) }
    var selectedDuration by remember { mutableIntStateOf(25) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FOCUS", letterSpacing = 2.sp, color = LanternGold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LanternGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LanternNight)
            )
        },
        containerColor = LanternNight
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (selectedKind == null) {
                Text(
                    "Choose your energy:",
                    style = MaterialTheme.typography.labelMedium,
                    color = LanternGold.copy(alpha = 0.6f)
                )
                
                LocalFocusModeCard(
                    title = "Deep Study",
                    description = "Focus on your hard subject or homework.",
                    icon = Icons.Default.Edit,
                    color = LanternGold,
                    onClick = { selectedKind = FocusKind.DEEP_WORK }
                )

                LocalFocusModeCard(
                    title = "Quiet Light",
                    description = "Take a break. Breathe and rest.",
                    icon = Icons.Default.WbSunny,
                    color = LanternBlue,
                    onClick = { 
                        selectedKind = FocusKind.MINDFULNESS
                        selectedDuration = 10
                    }
                )
            } else {
                Text(
                    text = if (selectedKind == FocusKind.DEEP_WORK) "STUDY FORGE" else "QUIET LIGHT",
                    style = MaterialTheme.typography.headlineSmall,
                    color = LanternGold,
                    fontWeight = FontWeight.Bold
                )

                if (selectedKind == FocusKind.DEEP_WORK) {
                    OutlinedTextField(
                        value = customTarget,
                        onValueChange = { customTarget = it },
                        label = { Text("Focusing on:", color = LanternGold.copy(alpha = 0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LanternGold,
                            unfocusedBorderColor = LanternGold.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(15, 25, 50).forEach { mins ->
                            LocalDurationOption(
                                minutes = mins,
                                selected = selectedDuration == mins,
                                color = LanternGold,
                                onClick = { selectedDuration = mins }
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(5, 10, 15, 20).forEach { mins ->
                            LocalDurationOption(
                                minutes = mins,
                                selected = selectedDuration == mins,
                                color = LanternBlue,
                                onClick = { selectedDuration = mins }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { 
                        if (selectedKind == FocusKind.DEEP_WORK) {
                            viewModel.startDeepWork(customTarget, selectedDuration)
                        } else {
                            viewModel.startMindfulness(selectedDuration)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedKind == FocusKind.DEEP_WORK) LanternGold else LanternBlue,
                        contentColor = LanternNight
                    )
                ) {
                    Text("START", fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }

                TextButton(
                    onClick = { selectedKind = null },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Change Mode", color = LanternGold.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun LocalFocusModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun LocalDurationOption(
    minutes: Int,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) color else Color.Transparent,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = Modifier.size(64.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = minutes.toString(),
                color = if (selected) LanternNight else color,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusSessionContent(
    viewModel: FocusViewModel,
    onBack: () -> Unit
) {
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val currentKind by viewModel.currentKind.collectAsState()
    val currentTarget by viewModel.currentTarget.collectAsState()
    
    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val themeColor = if (currentKind == FocusKind.DEEP_WORK) LanternGold else LanternBlue

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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (currentKind == FocusKind.DEEP_WORK) "STUDYING" else "RESTING",
                    style = MaterialTheme.typography.labelLarge,
                    color = themeColor.copy(alpha = 0.6f),
                    letterSpacing = 4.sp
                )
                if (currentTarget.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentTarget,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                style = MaterialTheme.typography.displayLarge,
                fontSize = 80.sp,
                color = if (timeRemaining <= 60 && timeRemaining > 0) LanternMiss else Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                IconButton(
                    onClick = { viewModel.pauseFocus() },
                    modifier = Modifier.size(72.dp).background(themeColor, CircleShape)
                ) {
                    Icon(Icons.Default.Pause, contentDescription = "Pause", tint = LanternNight, modifier = Modifier.size(32.dp))
                }
                
                TextButton(onClick = { viewModel.resetFocus() }) {
                    Text("END SESSION", color = themeColor.copy(alpha = 0.5f), letterSpacing = 1.sp)
                }
            }
        }
    }
}
