package com.obrien.thecathedral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.core.model.JournalEntry
import com.obrien.thecathedral.ui.theme.*
import com.obrien.thecathedral.viewmodel.JournalViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val today = LocalDate.now()
    val todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val todayEntry = uiState.journalEntries.find { it.date == todayStr }

    var p1 by remember { mutableStateOf(todayEntry?.morningCompleted ?: false) }
    var p2 by remember { mutableStateOf(todayEntry?.schoolCompleted ?: false) }
    var p3 by remember { mutableStateOf(todayEntry?.resetCompleted ?: false) }
    var p4 by remember { mutableStateOf(todayEntry?.studyCompleted ?: false) }
    var p5 by remember { mutableStateOf(todayEntry?.bodyCompleted ?: false) }
    var p6 by remember { mutableStateOf(todayEntry?.eveningCompleted ?: false) }
    
    var wentWell by remember { mutableStateOf(todayEntry?.wentWell ?: "") }
    var hardPart by remember { mutableStateOf(todayEntry?.hardPart ?: "") }
    var gratitude by remember { mutableStateOf(todayEntry?.gratitude ?: "") }

    LaunchedEffect(todayEntry) {
        todayEntry?.let {
            p1 = it.morningCompleted
            p2 = it.schoolCompleted
            p3 = it.resetCompleted
            p4 = it.studyCompleted
            p5 = it.bodyCompleted
            p6 = it.eveningCompleted
            wentWell = it.wentWell
            hardPart = it.hardPart
            gratitude = it.gratitude
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "THE CODEX",
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)),
                style = MaterialTheme.typography.labelMedium,
                color = CathedralGold.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )

            Text(
                text = "DAILY PILLARS",
                style = MaterialTheme.typography.titleSmall,
                color = CathedralGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            ScoreCardRow(label = "AWAKENING", checked = p1, onCheckedChange = { p1 = it })
            ScoreCardRow(label = "TECHNE", checked = p2, onCheckedChange = { p2 = it })
            ScoreCardRow(label = "HISTORIA", checked = p3, onCheckedChange = { p3 = it })
            ScoreCardRow(label = "GRIND", checked = p4, onCheckedChange = { p4 = it })
            ScoreCardRow(label = "GYMNOS", checked = p5, onCheckedChange = { p5 = it })
            ScoreCardRow(label = "SOPHIA", checked = p6, onCheckedChange = { p6 = it })

            HorizontalDivider(color = CathedralGold.copy(alpha = 0.2f))

            Text(
                text = "REFLECTION",
                style = MaterialTheme.typography.titleSmall,
                color = CathedralGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            JournalField(
                value = wentWell,
                onValueChange = { wentWell = it },
                label = "What went well?",
                placeholder = "A win, even a small one..."
            )

            JournalField(
                value = hardPart,
                onValueChange = { hardPart = it },
                label = "What was hard?",
                placeholder = "Where did you struggle?"
            )

            JournalField(
                value = gratitude,
                onValueChange = { gratitude = it },
                label = "One thing I’m grateful for",
                placeholder = "Name one good thing..."
            )

            Button(
                onClick = {
                    viewModel.saveJournalEntry(
                        JournalEntry(
                            date = todayStr,
                            morningCompleted = p1,
                            schoolCompleted = p2,
                            resetCompleted = p3,
                            studyCompleted = p4,
                            bodyCompleted = p5,
                            eveningCompleted = p6,
                            wentWell = wentWell,
                            hardPart = hardPart,
                            gratitude = gratitude,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CathedralGold,
                    contentColor = MonasteryBlack
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "SEAL ENTRY",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            HorizontalDivider(color = CathedralGold.copy(alpha = 0.2f))

            Text(
                text = "ARCHIVE",
                style = MaterialTheme.typography.titleSmall,
                color = CathedralGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            if (uiState.journalEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "“The unexamined life is not worth living.”",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Parchment.copy(alpha = 0.4f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Serif
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "— Socrates",
                            style = MaterialTheme.typography.labelSmall,
                            color = CathedralGold.copy(alpha = 0.3f)
                        )
                    }
                }
            } else {
                uiState.journalEntries.take(7).forEach { entry ->
                    HistoryCard(entry = entry)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun JournalField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = CathedralGold.copy(alpha = 0.6f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = Parchment.copy(alpha = 0.3f),
                    fontFamily = FontFamily.Serif
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CathedralGold,
                unfocusedBorderColor = CathedralGold.copy(alpha = 0.3f),
                focusedTextColor = Parchment,
                unfocusedTextColor = Parchment,
                focusedContainerColor = MonasteryBlack,
                unfocusedContainerColor = MonasteryBlack
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Serif,
                lineHeight = 22.sp
            )
        )
    }
}

@Composable
fun ScoreCardRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (checked) RitualSuccess else CathedralGold.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                if (checked) RitualSuccess.copy(alpha = 0.08f) else MonasteryBlack,
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = RitualSuccess,
                uncheckedColor = CathedralGold.copy(alpha = 0.5f),
                checkmarkColor = MonasteryBlack
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = if (checked) RitualSuccess else Parchment,
            fontFamily = FontFamily.Serif,
            fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun HistoryCard(entry: JournalEntry) {
    val date = try {
        LocalDate.parse(entry.date)
            .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    } catch (_: Exception) {
        entry.date
    }

    val score = entry.score

    Card(
        colors = CardDefaults.cardColors(containerColor = MonasteryBlack),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            CathedralGold.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = CathedralGold.copy(alpha = 0.6f)
                )
                Text(
                    text = "$score / 6",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (score == 6) RitualSuccess else CathedralGold,
                    fontWeight = FontWeight.Bold
                )
            }
            val reflection = listOf(entry.wentWell, entry.hardPart, entry.gratitude)
                .filter { it.isNotBlank() }
                .joinToString(" • ")
            if (reflection.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = reflection.take(120) + if (reflection.length > 120) "…" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Parchment.copy(alpha = 0.5f),
                    fontFamily = FontFamily.Serif,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
