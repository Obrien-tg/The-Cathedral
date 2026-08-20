package com.obrien.thecathedral.ui.screens

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
import com.obrien.thecathedral.data.ScheduleData
import com.obrien.core.model.Alarm
import com.obrien.core.model.Pillar
import com.obrien.core.model.PillarStatus
import com.obrien.core.ui.components.SunflowerParticle
import com.obrien.thecathedral.ui.theme.CathedralGold
import com.obrien.thecathedral.ui.theme.MonasteryBlack
import com.obrien.thecathedral.ui.theme.RitualMiss
import com.obrien.thecathedral.ui.theme.RitualSuccess
import com.obrien.thecathedral.ui.theme.TheCathedralTheme
import com.obrien.thecathedral.viewmodel.CodexViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScheduleScreen(
    viewModel: CodexViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "THE CATHEDRAL CODEX",
                            style = MaterialTheme.typography.titleMedium,
                            letterSpacing = 2.sp,
                            color = CathedralGold
                        )
                        if (uiState.weeklyIntention.weeklyAim.isNotBlank()) {
                            Text(
                                text = uiState.weeklyIntention.weeklyAim.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = CathedralGold.copy(alpha = 0.5f),
                                letterSpacing = 1.sp
                            )
                        }
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MonasteryBlack
                )
            )
        },
        containerColor = MonasteryBlack
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle sunflower in corner (Bug #4)
            SunflowerParticle(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp, end = 20.dp),
                size = 16f,
                drift = false,
                color = CathedralGold
            )

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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .border(
                width = 1.dp,
                color = CathedralGold.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MonasteryBlack)
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
                        color = CathedralGold.copy(alpha = 0.7f),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = pillar.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = CathedralGold,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Show Less" else "Show More",
                    tint = CathedralGold
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

    val statusColor = when (status) {
        PillarStatus.COMPLETE -> RitualSuccess
        PillarStatus.SKIPPED -> Color.Gray.copy(alpha = 0.6f)
        PillarStatus.MISSED -> RitualMiss
        PillarStatus.ACTIVE -> CathedralGold
        PillarStatus.PENDING -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    }

    Box(contentAlignment = Alignment.Center) {
        // Blessing Burst (Bug #4)
        if (showBlessing || blessingAlpha > 0f) {
            SunflowerParticle(
                modifier = Modifier
                    .size(48.dp)
                    .scale(blessingScale)
                    .alpha(blessingAlpha),
                size = 40f,
                drift = false,
                color = RitualSuccess
            )
        }

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
                                color = CathedralGold.copy(alpha = 0.6f)
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
                            tint = if (isCompleted) RitualSuccess else CathedralGold.copy(alpha = 0.4f)
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
    TheCathedralTheme(darkTheme = true) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "THE CATHEDRAL CODEX",
                            style = MaterialTheme.typography.titleMedium,
                            letterSpacing = 2.sp,
                            color = CathedralGold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MonasteryBlack)
                )
            },
            containerColor = MonasteryBlack
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
                                color = CathedralGold.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = MonasteryBlack)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = pillar.timeRange,
                                style = MaterialTheme.typography.labelSmall,
                                color = CathedralGold.copy(alpha = 0.7f),
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = pillar.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = CathedralGold,
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
