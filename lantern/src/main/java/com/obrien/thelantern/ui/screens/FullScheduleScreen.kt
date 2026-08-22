package com.obrien.thelantern.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.thelantern.data.ScheduleData
import com.obrien.core.model.Alarm
import com.obrien.core.model.Pillar
import com.obrien.core.model.PillarStatus
import com.obrien.thelantern.ui.theme.LanternNight
import com.obrien.thelantern.ui.theme.LanternMiss
import com.obrien.thelantern.ui.theme.LanternSuccess
import com.obrien.thelantern.ui.theme.LumiTheme
import com.obrien.thelantern.viewmodel.CodexViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScheduleScreen(
    viewModel: CodexViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val primary = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "MY FULL DAY",
                        style = MaterialTheme.typography.titleMedium,
                        letterSpacing = 2.sp,
                        color = primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LanternNight
                )
            )
        },
        containerColor = LanternNight
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(uiState.pillars) { pillar ->
                    PillarItem(
                        pillar = pillar,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun PillarItem(
    pillar: Pillar,
    viewModel: CodexViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val primary = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .border(
                width = 1.dp,
                color = primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = LanternNight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pillar.timeRange,
                        style = MaterialTheme.typography.labelSmall,
                        color = primary.copy(alpha = 0.7f),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = pillar.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = primary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Show Less" else "Show More",
                    tint = primary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    pillar.alarms.forEach { alarm ->
                        AlarmDetail(
                            alarm = alarm,
                            viewModel = viewModel
                        )
                        if (alarm != pillar.alarms.last()) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmDetail(
    alarm: Alarm,
    viewModel: CodexViewModel
) {
    val haptic = LocalHapticFeedback.current
    val status = viewModel.getAlarmStatus(alarm)
    val isCompleted = status == PillarStatus.COMPLETE
    val isSkipped = status == PillarStatus.SKIPPED

    var showBlessing by remember { mutableStateOf(false) }

    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            showBlessing = true
            delay(600)
            showBlessing = false
        }
    }

    val blessingScale by animateFloatAsState(
        targetValue = if (showBlessing) 1.5f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "scale"
    )
    val blessingAlpha by animateFloatAsState(
        targetValue = if (showBlessing) 0.8f else 0f,
        animationSpec = spring(),
        label = "alpha"
    )

    val primary = MaterialTheme.colorScheme.primary
    val statusColor = when (status) {
        PillarStatus.COMPLETE -> LanternSuccess
        PillarStatus.SKIPPED -> Color.Gray.copy(alpha = 0.6f)
        PillarStatus.MISSED -> LanternMiss
        PillarStatus.ACTIVE -> primary
        PillarStatus.PENDING -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    }

    Box(contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.alpha(if (isSkipped) 0.5f else 1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(statusColor, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${alarm.time} — ${alarm.name}${if (isSkipped) " (SKIPPED)" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row {
                    if (!isCompleted) {
                        TextButton(
                            onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.toggleSkip(alarm.id) 
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = if (isSkipped) "RESTORE" else "SKIP",
                                fontSize = 10.sp,
                                color = primary.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    IconButton(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                            if (!isSkipped) viewModel.toggleAlarm(alarm.id) else viewModel.toggleSkip(alarm.id) 
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .semantics {
                                stateDescription = if (isCompleted) "Completed" else if (isSkipped) "Skipped" else "Pending"
                            }
                    ) {
                        Icon(
                            imageVector = if (isCompleted)
                                Icons.Filled.CheckCircle
                            else
                                Icons.Outlined.RadioButtonUnchecked,
                            contentDescription = if (isCompleted) "Mark incomplete" else "Mark complete",
                            tint = if (isCompleted) LanternSuccess else primary.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            alarm.tasks.forEach { task ->
                Text(
                    text = "• $task",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCompleted || isSkipped)
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    else
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun FullScheduleScreenPreview() {
    LumiTheme(darkTheme = true) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "LUMI CODEX",
                            style = MaterialTheme.typography.titleMedium,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = LanternNight)
                )
            },
            containerColor = LanternNight
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(ScheduleData.pillars) { pillar ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = LanternNight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = pillar.timeRange,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = pillar.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }
                }
            }
        }
    }
}
