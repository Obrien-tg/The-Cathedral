package com.obrien.thelantern.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.thelantern.data.ScheduleData
import com.obrien.thelantern.data.ScheduleShaper
import com.obrien.thelantern.domain.usecase.DailyScore
import com.obrien.thelantern.model.DailyCounsel
import com.obrien.core.model.Pillar
import com.obrien.core.model.WeeklyIntention
import com.obrien.thelantern.ui.theme.AmbientDust
import com.obrien.thelantern.ui.theme.LanternBlue
import com.obrien.thelantern.ui.theme.LanternNight
import com.obrien.thelantern.ui.theme.LanternText
import com.obrien.thelantern.ui.theme.LumiTheme
import com.obrien.thelantern.ui.theme.glassCard
import com.obrien.thelantern.viewmodel.HomeUiState
import com.obrien.thelantern.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onViewFullSchedule: () -> Unit = {},
    onFocusMode: () -> Unit = {},
    onJournal: () -> Unit = {},
    onPhilosophy: () -> Unit = {},
    onSkillTree: () -> Unit = {},
    onWeeklyReview: () -> Unit = {},
    onWeeklyIntention: () -> Unit = {},
    onSettings: () -> Unit = {},
    onHomework: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showResetConfirm by remember { mutableStateOf(false) }

    if (showResetConfirm) {
        val primary = MaterialTheme.colorScheme.primary
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("PURGE ALL PROGRESS?", color = primary) },
            text = { Text("This will clear today's rituals and reading progress. The historical record remains untouched.", color = Color.White) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllProgress()
                    showResetConfirm = false
                }) { Text("PURGE", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text("CANCEL", color = primary) }
            },
            containerColor = LanternNight
        )
    }

    HomeScreenContent(
        modifier = modifier,
        uiState = uiState,
        onViewFullSchedule = onViewFullSchedule,
        onFocusMode = onFocusMode,
        onJournal = onJournal,
        onPhilosophy = onPhilosophy,
        onSkillTree = onSkillTree,
        onWeeklyReview = onWeeklyReview,
        onWeeklyIntention = onWeeklyIntention,
        onSettings = onSettings,
        onHomework = onHomework,
        onDismissAccountability = { viewModel.acknowledgeAccountability() },
        onResetDay = { showResetConfirm = true }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onViewFullSchedule: () -> Unit = {},
    onFocusMode: () -> Unit = {},
    onJournal: () -> Unit = {},
    onPhilosophy: () -> Unit = {},
    onSkillTree: () -> Unit = {},
    onWeeklyReview: () -> Unit = {},
    onWeeklyIntention: () -> Unit = {},
    onSettings: () -> Unit = {},
    onHomework: () -> Unit = {},
    onDismissAccountability: () -> Unit = {},
    onResetDay: () -> Unit = {}
) {
    if (uiState.showAccountabilityDialog) {
        val primary = MaterialTheme.colorScheme.primary
        AlertDialog(
            onDismissRequest = onDismissAccountability,
            title = {
                Text(
                    "LUMI",
                    style = MaterialTheme.typography.titleMedium,
                    color = primary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Yesterday was a rest day. That's okay. Today is new.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = """"${ScheduleData.PURPOSE_STATEMENT}"""",
                        style = MaterialTheme.typography.bodySmall,
                        color = primary.copy(alpha = 0.7f),
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Are you here?",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onDismissAccountability) {
                    Text("I'M HERE", color = primary)
                }
            },
            containerColor = LanternNight
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LanternNight,
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AmbientDust()

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
                    WeekRuleCard(
                        hasWeekRule = uiState.hasWeekRule,
                        intention = uiState.weeklyIntention,
                        onClick = onWeeklyIntention
                    )
                }

                item { DailyCounselCard(counsel = uiState.todayCounsel) }

                if (uiState.activePillar?.id == "reset" || uiState.activePillar?.id == "study") {
                    item {
                        HomeworkPromptCard(onClick = onHomework)
                    }
                }

                item {
                    val isSunday = java.time.LocalDate.now().dayOfWeek == java.time.DayOfWeek.SUNDAY
                    if (isSunday) {
                        val primary = MaterialTheme.colorScheme.primary
                        Button(
                            onClick = onWeeklyReview,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primary,
                                contentColor = LanternNight
                            )
                        ) {
                            Icon(Icons.Default.AutoStories, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("COMMENCE WEEKLY REVIEW", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }

                item {
                    val pillar = uiState.activePillar
                    if (pillar != null) {
                        ActivePillarSection(
                            pillar = pillar,
                            isActive = true,
                            morningPrompt = uiState.todayCounsel.morningPrompt,
                            eveningPrompt = uiState.todayCounsel.eveningPrompt
                        )
                    } else {
                        uiState.nextPillar?.let {
                            ActivePillarSection(
                                pillar = it,
                                isActive = false,
                                morningPrompt = uiState.todayCounsel.morningPrompt,
                                eveningPrompt = uiState.todayCounsel.eveningPrompt
                            )
                        } ?: RestSection()
                    }
                }

                item {
                    ProgressSection(
                        completed = uiState.score.completedCount,
                        total = uiState.score.totalCount
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
                            label = "MY DAY",
                            onClick = onViewFullSchedule,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = Icons.Default.AutoStories,
                            label = "MY WORDS",
                            onClick = onPhilosophy,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    QuickActionButton(
                        icon = Icons.Default.AccountTree,
                        label = "MY PATH",
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
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
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
    val primary = MaterialTheme.colorScheme.primary
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = primary
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(primary.copy(alpha = 0.3f))
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = primary,
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
    val primary = MaterialTheme.colorScheme.primary
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "THE PURPOSE",
            style = MaterialTheme.typography.labelMedium,
            color = primary,
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
        HorizontalDivider(color = primary.copy(alpha = 0.3f), thickness = 1.dp)
    }
}

@Composable
fun ActivePillarSection(pillar: Pillar, isActive: Boolean, morningPrompt: String? = null, eveningPrompt: String? = null) {
    val primary = MaterialTheme.colorScheme.primary
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
                text = if (isActive) "RIGHT NOW" else "UPCOMING",
                style = MaterialTheme.typography.labelSmall,
                color = primary.copy(alpha = 0.7f)
            )
            Text(
                text = pillar.name,
                style = MaterialTheme.typography.headlineSmall,
                color = primary,
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

            val prompt = if (pillar.id == "morning") morningPrompt else if (pillar.id == "evening") eveningPrompt else null
            if (prompt != null) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = primary.copy(alpha = 0.2f), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "THINKING ABOUT",
                    style = MaterialTheme.typography.labelSmall,
                    color = primary.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = prompt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}

@Composable
fun NextPillarSection(pillar: Pillar) {
    val primary = MaterialTheme.colorScheme.primary
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
                text = "NEXT",
                style = MaterialTheme.typography.labelSmall,
                color = primary.copy(alpha = 0.5f)
            )
            Text(
                text = pillar.name,
                style = MaterialTheme.typography.headlineSmall,
                color = primary.copy(alpha = 0.8f),
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
    val primary = MaterialTheme.colorScheme.primary
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
                text = "SOFT REST",
                style = MaterialTheme.typography.labelSmall,
                color = primary.copy(alpha = 0.5f)
            )
            Text(
                text = "Rest. The day is done.",
                style = MaterialTheme.typography.headlineSmall,
                color = primary.copy(alpha = 0.6f),
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
    val primary = MaterialTheme.colorScheme.primary
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "TODAY'S STEPS: $completed OF $total",
            style = MaterialTheme.typography.labelMedium,
            color = primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { if (total > 0) completed.toFloat() / total else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = primary,
            trackColor = primary.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun DailyCounselCard(counsel: DailyCounsel) {
    val primary = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = counsel.theme.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = primary.copy(alpha = 0.6f),
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = """"${counsel.quote}"""",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "— ${counsel.author}, ${counsel.source}",
                style = MaterialTheme.typography.labelSmall,
                color = primary.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeekRuleCard(
    hasWeekRule: Boolean,
    intention: WeeklyIntention,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val primary = MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (hasWeekRule) primary.copy(alpha = 0.35f) else LanternBlue.copy(alpha = 0.45f), shape)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = if (hasWeekRule) "THIS WEEK'S RULE" else "SET THIS WEEK'S RULE",
            color = primary,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (!hasWeekRule) {
            Text(
                text = "Name what you are building, reading, and researching. The schedule will speak those names.",
                color = LanternText.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap to set →",
                color = primary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelMedium
            )
        } else {
            val rows = ScheduleShaper.focusSummary(intention)
            rows.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = label,
                        color = LanternBlue,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.width(72.dp)
                    )
                    Text(
                        text = value,
                        color = LanternText.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap to revise →",
                color = primary.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun HomeworkPromptCard(onClick: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = primary.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, primary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = primary.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AutoStories, contentDescription = null, tint = primary)
                }
            }
            Column {
                Text(
                    "Any homework today?",
                    style = MaterialTheme.typography.titleMedium,
                    color = primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Tap to plan your study forge",
                    style = MaterialTheme.typography.bodySmall,
                    color = LanternText.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LumiTheme(darkTheme = true) {
        HomeScreenContent(
            uiState = HomeUiState(
                activePillar = ScheduleData.pillars.first(),
                score = DailyScore(3, 15, 0.2f)
            )
        )
    }
}
