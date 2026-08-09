package com.obrien.thecathedral.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.thecathedral.data.ScheduleData
import com.obrien.thecathedral.model.Pillar
import com.obrien.thecathedral.ui.theme.CathedralGold
import com.obrien.thecathedral.ui.theme.MonasteryBlack
import com.obrien.thecathedral.ui.theme.TheCathedralTheme
import com.obrien.thecathedral.viewmodel.CathedralUiState
import com.obrien.thecathedral.viewmodel.ScheduleViewModel

import androidx.compose.material.icons.filled.AccountTree
import com.obrien.thecathedral.ui.components.PillarProgressRing
import com.obrien.thecathedral.ui.components.SunflowerParticle
import com.obrien.thecathedral.ui.theme.AmbientDust
import com.obrien.thecathedral.ui.theme.glassCard

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel,
    onViewFullSchedule: () -> Unit = {},
    onFocusMode: () -> Unit = {},
    onJournal: () -> Unit = {},
    onPhilosophy: () -> Unit = {},
    onSkillTree: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    HomeScreenContent(
        modifier = modifier,
        uiState = uiState,
        onViewFullSchedule = onViewFullSchedule,
        onFocusMode = onFocusMode,
        onJournal = onJournal,
        onPhilosophy = onPhilosophy,
        onSkillTree = onSkillTree,
        onResetDay = { viewModel.clearAllProgress() }
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    uiState: CathedralUiState,
    onViewFullSchedule: () -> Unit = {},
    onFocusMode: () -> Unit = {},
    onJournal: () -> Unit = {},
    onPhilosophy: () -> Unit = {},
    onSkillTree: () -> Unit = {},
    onResetDay: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MonasteryBlack
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AmbientDust()

            // Subtle sunflower charm — top right, floating (Bug #4)
            SunflowerParticle(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 20.dp),
                size = 20f
            )

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item { PurposeSection() }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PillarProgressRing(percentage = 0.5f, pillarName = "TECHNE", level = 1, modifier = Modifier.weight(1f))
                        PillarProgressRing(percentage = 0.3f, pillarName = "HISTORIA", level = 1, modifier = Modifier.weight(1f))
                        PillarProgressRing(percentage = 0.8f, pillarName = "GYMNOS", level = 2, modifier = Modifier.weight(1f))
                        PillarProgressRing(percentage = 0.2f, pillarName = "SOPHIA", level = 1, modifier = Modifier.weight(1f))
                    }
                }

                item {
                    val pillar = uiState.activePillar
                    if (pillar != null) {
                        ActivePillarSection(pillar = pillar, isActive = true)
                    } else {
                        uiState.nextPillar?.let {
                            NextPillarSection(pillar = it)
                        } ?: RestSection()
                    }
                }

                item {
                    ProgressSection(
                        completed = uiState.completedCount,
                        total = uiState.totalCount
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.Timer,
                            label = "FOCUS",
                            onClick = onFocusMode,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = Icons.Default.Edit,
                            label = "JOURNAL",
                            onClick = onJournal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionButton(
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            label = "SCHEDULE",
                            onClick = onViewFullSchedule,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = Icons.Default.AutoStories,
                            label = "PHILOSOPHY",
                            onClick = onPhilosophy,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    QuickActionButton(
                        icon = Icons.Default.AccountTree,
                        label = "SKILL TREE",
                        onClick = onSkillTree,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    TextButton(
                        onClick = onResetDay,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            "Reset Day",
                            color = CathedralGold.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = CathedralGold
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(CathedralGold.copy(alpha = 0.3f))
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = CathedralGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
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
fun ActivePillarSection(pillar: Pillar, isActive: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isActive) "CURRENT PILLAR" else "UPCOMING PILLAR",
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
fun NextPillarSection(pillar: Pillar) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NEXT PILLAR",
                style = MaterialTheme.typography.labelSmall,
                color = CathedralGold.copy(alpha = 0.5f)
            )
            Text(
                text = pillar.name,
                style = MaterialTheme.typography.headlineSmall,
                color = CathedralGold.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = pillar.timeRange,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun RestSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "THE SANCTUARY",
                style = MaterialTheme.typography.labelSmall,
                color = CathedralGold.copy(alpha = 0.5f)
            )
            Text(
                text = "Rest. The day is done.",
                style = MaterialTheme.typography.headlineSmall,
                color = CathedralGold.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ScheduleData.MANTRA,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif
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
        HomeScreenContent(
            uiState = CathedralUiState(
                activePillar = ScheduleData.pillars.first(),
                completedCount = 3,
                totalCount = 15
            )
        )
    }
}
