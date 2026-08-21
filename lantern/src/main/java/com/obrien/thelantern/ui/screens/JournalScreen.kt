package com.obrien.thelantern.ui.screens

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
import com.obrien.thelantern.ui.theme.LanternNight
import com.obrien.thelantern.ui.theme.LanternText
import com.obrien.thelantern.ui.theme.LanternSuccess
import com.obrien.thelantern.ui.theme.TheLanternTheme
import com.obrien.thelantern.viewmodel.JournalViewModel
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
    val primary = MaterialTheme.colorScheme.primary

    var morning by remember { mutableStateOf(todayEntry?.morningCompleted ?: false) }
    var school by remember { mutableStateOf(todayEntry?.schoolCompleted ?: false) }
    var reset by remember { mutableStateOf(todayEntry?.resetCompleted ?: false) }
    var study by remember { mutableStateOf(todayEntry?.studyCompleted ?: false) }
    var body by remember { mutableStateOf(todayEntry?.bodyCompleted ?: false) }
    var evening by remember { mutableStateOf(todayEntry?.eveningCompleted ?: false) }
    
    var mindEffort by remember { mutableStateOf(todayEntry?.mindEffort ?: false) }
    var bodyEffort by remember { mutableStateOf(todayEntry?.bodyEffort ?: false) }
    var characterEffort by remember { mutableStateOf(todayEntry?.characterEffort ?: false) }
    
    var wentWell by remember { mutableStateOf(todayEntry?.wentWell ?: "") }
    var hardPart by remember { mutableStateOf(todayEntry?.hardPart ?: "") }
    var gratitude by remember { mutableStateOf(todayEntry?.gratitude ?: "") }

    LaunchedEffect(todayEntry) {
        todayEntry?.let {
            morning = it.morningCompleted
            school = it.schoolCompleted
            reset = it.resetCompleted
            study = it.studyCompleted
            body = it.bodyCompleted
            evening = it.eveningCompleted
            mindEffort = it.mindEffort
            bodyEffort = it.bodyEffort
            characterEffort = it.characterEffort
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LanternNight)
            )
        },
        containerColor = LanternNight
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
                color = primary.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )

            Text(
                text = "DAILY PILLARS",
                style = MaterialTheme.typography.titleSmall,
                color = primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ScoreCardRow(label = "Morning Start", checked = morning, onCheckedChange = { morning = it })
                ScoreCardRow(label = "School Day", checked = school, onCheckedChange = { school = it })
                ScoreCardRow(label = "After-School Reset", checked = reset, onCheckedChange = { reset = it })
                ScoreCardRow(label = "Study Forge", checked = study, onCheckedChange = { study = it })
                ScoreCardRow(label = "Body & Belonging", checked = body, onCheckedChange = { body = it })
                ScoreCardRow(label = "Evening Close", checked = evening, onCheckedChange = { evening = it })
            }

            HorizontalDivider(color = primary.copy(alpha = 0.2f))

            Text(
                text = "EFFORT",
                style = MaterialTheme.typography.titleSmall,
                color = primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EffortChip(label = "Mind", selected = mindEffort, onToggle = { mindEffort = it }, modifier = Modifier.weight(1f))
                EffortChip(label = "Body", selected = bodyEffort, onToggle = { bodyEffort = it }, modifier = Modifier.weight(1f))
                EffortChip(label = "Character", selected = characterEffort, onToggle = { characterEffort = it }, modifier = Modifier.weight(1f))
            }

            HorizontalDivider(color = primary.copy(alpha = 0.2f))

            Text(
                text = "REFLECTION",
                style = MaterialTheme.typography.titleSmall,
                color = primary,
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
                            morningCompleted = morning,
                            schoolCompleted = school,
                            resetCompleted = reset,
                            studyCompleted = study,
                            bodyCompleted = body,
                            eveningCompleted = evening,
                            mindEffort = mindEffort,
                            bodyEffort = bodyEffort,
                            characterEffort = characterEffort,
                            wentWell = wentWell,
                            hardPart = hardPart,
                            gratitude = gratitude,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = LanternNight
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "SEAL ENTRY",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            HorizontalDivider(color = primary.copy(alpha = 0.2f))

            Text(
                text = "ARCHIVE",
                style = MaterialTheme.typography.titleSmall,
                color = primary,
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
                            text = "“I am still becoming. That is how greatness begins.”",
                            style = MaterialTheme.typography.bodyLarge,
                            color = LanternText.copy(alpha = 0.4f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Serif
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Begin your first entry above.",
                            style = MaterialTheme.typography.labelMedium,
                            color = primary,
                            letterSpacing = 1.sp
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
fun EffortChip(
    label: String,
    selected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    FilterChip(
        selected = selected,
        onClick = { onToggle(!selected) },
        label = { Text(label, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = primary,
            selectedLabelColor = LanternNight,
            containerColor = LanternNight,
            labelColor = primary.copy(alpha = 0.6f)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = primary.copy(alpha = 0.3f),
            selectedBorderColor = primary
        )
    )
}

@Composable
fun JournalField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    val primary = MaterialTheme.colorScheme.primary
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = primary.copy(alpha = 0.6f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = LanternText.copy(alpha = 0.3f),
                    fontFamily = FontFamily.Serif
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primary,
                unfocusedBorderColor = primary.copy(alpha = 0.3f),
                focusedTextColor = LanternText,
                unfocusedTextColor = LanternText,
                focusedContainerColor = LanternNight,
                unfocusedContainerColor = LanternNight
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
    val primary = MaterialTheme.colorScheme.primary
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (checked) LanternSuccess else primary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                if (checked) LanternSuccess.copy(alpha = 0.08f) else LanternNight,
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = LanternSuccess,
                uncheckedColor = primary.copy(alpha = 0.5f),
                checkmarkColor = LanternNight
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = if (checked) LanternSuccess else LanternText,
            fontFamily = FontFamily.Serif,
            fontSize = 14.sp,
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
    val primary = MaterialTheme.colorScheme.primary

    Card(
        colors = CardDefaults.cardColors(containerColor = LanternNight),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            primary.copy(alpha = 0.15f)
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
                    color = primary.copy(alpha = 0.6f)
                )
                Text(
                    text = "$score / 6",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (score == 6) LanternSuccess else primary,
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
                    color = LanternText.copy(alpha = 0.5f),
                    fontFamily = FontFamily.Serif,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
