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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.thecathedral.model.JournalEntry
import com.obrien.thecathedral.ui.theme.CathedralGold
import com.obrien.thecathedral.ui.theme.MonasteryBlack
import com.obrien.thecathedral.ui.theme.Parchment
import com.obrien.thecathedral.ui.theme.RitualSuccess
import com.obrien.thecathedral.ui.theme.TheCathedralTheme
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

    var techne by remember { mutableStateOf(todayEntry?.techneCompleted ?: false) }
    var historia by remember { mutableStateOf(todayEntry?.historiaCompleted ?: false) }
    var gymnoso by remember { mutableStateOf(todayEntry?.gymnosoCompleted ?: false) }
    var sophia by remember { mutableStateOf(todayEntry?.sophiaCompleted ?: false) }
    var freeText by remember { mutableStateOf(todayEntry?.freeText ?: "") }
    var learning by remember { mutableStateOf(todayEntry?.learning ?: "") }
    var improvement by remember { mutableStateOf(todayEntry?.improvement ?: "") }

    LaunchedEffect(todayEntry) {
        todayEntry?.let {
            techne = it.techneCompleted
            historia = it.historiaCompleted
            gymnoso = it.gymnosoCompleted
            sophia = it.sophiaCompleted
            freeText = it.freeText
            learning = it.learning
            improvement = it.improvement
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
                text = "DAILY SCORECARD",
                style = MaterialTheme.typography.titleSmall,
                color = CathedralGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            ScoreCardRow(
                label = "⚙️ TECHNE — The Forge",
                checked = techne,
                onCheckedChange = { techne = it }
            )
            ScoreCardRow(
                label = "📜 HISTORIA — The Archive",
                checked = historia,
                onCheckedChange = { historia = it }
            )
            ScoreCardRow(
                label = "🏛️ GYMNOS — The Arena",
                checked = gymnoso,
                onCheckedChange = { gymnoso = it }
            )
            ScoreCardRow(
                label = "🌙 SOPHIA — The Sanctuary",
                checked = sophia,
                onCheckedChange = { sophia = it }
            )

            HorizontalDivider(color = CathedralGold.copy(alpha = 0.2f))

            Text(
                text = "REFLECTION",
                style = MaterialTheme.typography.titleSmall,
                color = CathedralGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            JournalField(
                value = freeText,
                onValueChange = { freeText = it },
                label = "What did you build today?",
                placeholder = "Describe your labour..."
            )

            JournalField(
                value = learning,
                onValueChange = { learning = it },
                label = "What did you learn?",
                placeholder = "A truth discovered or reinforced..."
            )

            JournalField(
                value = improvement,
                onValueChange = { improvement = it },
                label = "How will you be better tomorrow?",
                placeholder = "The specific correction..."
            )

            Button(
                onClick = {
                    viewModel.saveJournalEntry(
                        JournalEntry(
                            date = todayStr,
                            techneCompleted = techne,
                            historiaCompleted = historia,
                            gymnosoCompleted = gymnoso,
                            sophiaCompleted = sophia,
                            freeText = freeText,
                            learning = learning,
                            improvement = improvement,
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
                Text(
                    text = "No entries yet. Begin today.",
                    color = Parchment.copy(alpha = 0.4f),
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
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

    val score = listOf(
        entry.techneCompleted,
        entry.historiaCompleted,
        entry.gymnosoCompleted,
        entry.sophiaCompleted
    ).count { it }

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
                    text = "$score / 4",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (score == 4) RitualSuccess else CathedralGold,
                    fontWeight = FontWeight.Bold
                )
            }
            if (entry.freeText.isNotBlank() || entry.learning.isNotBlank() || entry.improvement.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                val summary = listOf(entry.freeText, entry.learning, entry.improvement)
                    .filter { it.isNotBlank() }
                    .joinToString(" • ")
                Text(
                    text = summary.take(120) + if (summary.length > 120) "…" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Parchment.copy(alpha = 0.5f),
                    fontFamily = FontFamily.Serif,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun JournalScreenPreview() {
    TheCathedralTheme(darkTheme = true) {
        Scaffold(containerColor = MonasteryBlack) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("DAILY SCORECARD", color = CathedralGold)
                ScoreCardRow("⚙️ TECHNE", checked = true) {}
                ScoreCardRow("📜 HISTORIA", checked = false) {}
            }
        }
    }
}
