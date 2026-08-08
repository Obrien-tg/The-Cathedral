package com.example.thecathedral.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecathedral.data.ScheduleData
import com.example.thecathedral.model.Alarm
import com.example.thecathedral.model.Pillar
import com.example.thecathedral.model.PillarStatus
import com.example.thecathedral.ui.theme.CathedralGold
import com.example.thecathedral.ui.theme.MonasteryBlack
import com.example.thecathedral.ui.theme.RitualMiss
import com.example.thecathedral.ui.theme.RitualSuccess
import com.example.thecathedral.ui.theme.TheCathedralTheme
import com.example.thecathedral.viewmodel.ScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScheduleScreen(
    viewModel: ScheduleViewModel,
    onBack: () -> Unit = {}
) {
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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(ScheduleData.pillars) { pillar ->
                PillarItem(
                    pillar = pillar,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun PillarItem(
    pillar: Pillar,
    viewModel: ScheduleViewModel
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
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmDetail(
    alarm: Alarm,
    viewModel: ScheduleViewModel
) {
    val status = viewModel.getAlarmStatus(alarm)
    val isCompleted = status == PillarStatus.COMPLETE

    val statusColor = when (status) {
        PillarStatus.COMPLETE -> RitualSuccess
        PillarStatus.MISSED -> RitualMiss
        PillarStatus.ACTIVE -> CathedralGold
        PillarStatus.PENDING -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    }

    Column {
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
                    text = "${alarm.time} — ${alarm.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = { viewModel.toggleAlarm(alarm.id) },
                modifier = Modifier.size(32.dp)
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

        alarm.tasks.forEach { task ->
            Text(
                text = "• $task",
                style = MaterialTheme.typography.bodySmall,
                color = if (isCompleted)
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
            )
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
