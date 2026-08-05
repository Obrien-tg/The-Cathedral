package com.example.thecathedral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecathedral.data.ScheduleData
import com.example.thecathedral.model.Pillar
import com.example.thecathedral.model.PillarStatus
import com.example.thecathedral.ui.theme.CathedralGold
import com.example.thecathedral.ui.theme.MonasteryBlack
import com.example.thecathedral.ui.theme.TheCathedralTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onViewFullSchedule: () -> Unit = {}
) {
    val pillars = ScheduleData.pillars
    val completedCount = pillars.flatMap { it.alarms }.count { it.status == PillarStatus.COMPLETE }
    val totalCount = pillars.flatMap { it.alarms }.size

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MonasteryBlack
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // 1. Purpose Statement
            item {
                PurposeSection()
            }

            // 2. Current Active Pillar
            item {
                ActivePillarSection(pillar = pillars.first()) // Mocking active for now
            }

            // 3. Progress Section
            item {
                ProgressSection(completed = completedCount, total = totalCount)
            }

            // 4. Action Button
            item {
                Button(
                    onClick = onViewFullSchedule,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CathedralGold,
                        contentColor = MonasteryBlack
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "VIEW FULL SCHEDULE",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PurposeSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "THE PURPOSE",
            style = MaterialTheme.typography.labelMedium,
            color = CathedralGold,
            letterSpacing = 3.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = ScheduleData.PURPOSE_STATEMENT,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Serif,
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = CathedralGold.copy(alpha = 0.3f), thickness = 1.dp)
    }
}

@Composable
fun ActivePillarSection(pillar: Pillar) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = AssistChipDefaults.assistChipBorder(borderColor = CathedralGold, enabled = true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CURRENT PILLAR",
                style = MaterialTheme.typography.labelSmall,
                color = CathedralGold.copy(alpha = 0.7f)
            )
            Text(
                text = pillar.name,
                style = MaterialTheme.typography.headlineSmall,
                color = CathedralGold,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = pillar.timeRange,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun ProgressSection(completed: Int, total: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "DAILY SCORE: $completed OF $total",
            style = MaterialTheme.typography.labelMedium,
            color = CathedralGold
        )
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { if (total > 0) completed.toFloat() / total else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = CathedralGold,
            trackColor = CathedralGold.copy(alpha = 0.1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TheCathedralTheme(darkTheme = true) {
        HomeScreen()
    }
}
