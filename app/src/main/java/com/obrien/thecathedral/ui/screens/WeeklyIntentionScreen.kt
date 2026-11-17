package com.obrien.thecathedral.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.thecathedral.data.DayScheduler
import com.obrien.thecathedral.ui.theme.CathedralGold
import com.obrien.thecathedral.ui.theme.MonasteryBlack
import com.obrien.thecathedral.ui.theme.Parchment
import com.obrien.thecathedral.viewmodel.WeeklyIntentionUiState
import com.obrien.thecathedral.viewmodel.WeeklyIntentionViewModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyIntentionScreen(
    viewModel: WeeklyIntentionViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val structuralError = uiState.structuralError

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "SEAL THE WEEK\u2019S RULE",
                            style = MaterialTheme.typography.titleMedium,
                            letterSpacing = 3.sp,
                            color = CathedralGold
                        )
                        Text(
                            if (uiState.isUpcomingWeek)
                                "Week of ${uiState.weekStartDate} — begins tomorrow"
                            else
                                "Week of ${uiState.weekStartDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CathedralGold.copy(alpha = 0.5f)
                        )
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
                actions = {
                    IconButton(onClick = { viewModel.clear() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color.Red.copy(alpha = 0.6f))
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (uiState.isAnythingSet()) {
                IntentionPreview(uiState)
            }

            // ---- NEW: the structural vows, asked before the quests ----
            IntentionSection(title = "THE HOURS") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        TimeVowPicker(
                            label = "I will rise at",
                            value = uiState.targetWakeTime,
                            fallback = LocalTime.of(7, 0),
                            onChange = { viewModel.updateTargetWakeTime(it) }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        TimeVowPicker(
                            label = "The day ends at",
                            value = uiState.targetSleepTime,
                            fallback = LocalTime.of(22, 0),
                            onChange = { viewModel.updateTargetSleepTime(it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "The Forge\u2019s due (deep work per day)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Parchment.copy(alpha = 0.4f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilledIconButton(
                        onClick = { viewModel.adjustFocusHours(-0.5f) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = CathedralGold.copy(alpha = 0.12f),
                            contentColor = CathedralGold
                        )
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Less")
                    }
                    Text(
                        text = "${uiState.focusLabel}h",
                        style = MaterialTheme.typography.headlineSmall,
                        color = CathedralGold,
                        fontFamily = FontFamily.Serif
                    )
                    FilledIconButton(
                        onClick = { viewModel.adjustFocusHours(0.5f) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = CathedralGold.copy(alpha = 0.12f),
                            contentColor = CathedralGold
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "More")
                    }
                    Text(
                        text = "hours, honestly protected",
                        style = MaterialTheme.typography.bodySmall,
                        color = Parchment.copy(alpha = 0.4f),
                        fontStyle = FontStyle.Italic
                    )
                }

                if (structuralError != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = structuralError,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCF6A6A),
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            IntentionSection(title = "THE FORGE (TECHNE)") {
                IntentionField(
                    label = "Build Quest",
                    value = uiState.techneFocus,
                    onValueChange = { viewModel.updateTechneFocus(it) },
                    placeholder = "e.g. Liberty Timeline – event list"
                )
            }

            IntentionSection(title = "THE ARCHIVE (HISTORIA)") {
                IntentionField(
                    label = "Book of the Week",
                    value = uiState.historiaBook,
                    onValueChange = { viewModel.updateHistoriaBook(it) },
                    placeholder = "e.g. Thucydides – Book I"
                )
                Spacer(modifier = Modifier.height(12.dp))
                IntentionField(
                    label = "Idea to Ponder",
                    value = uiState.historiaResearch,
                    onValueChange = { viewModel.updateHistoriaResearch(it) },
                    placeholder = "e.g. Pericles and civic speech"
                )
            }

            IntentionSection(title = "THE ARENA (GYMNOS)") {
                IntentionField(
                    label = "Body / Play Quest",
                    value = uiState.gymnosFocus,
                    onValueChange = { viewModel.updateGymnosFocus(it) },
                    placeholder = "e.g. Zone 2 walks / Squat form"
                )
            }

            IntentionSection(title = "THE SANCTUARY (SOPHIA)") {
                IntentionField(
                    label = "Theme / Question",
                    value = uiState.sophiaTheme,
                    onValueChange = { viewModel.updateSophiaTheme(it) },
                    placeholder = "e.g. Fewer inputs, longer silence"
                )
            }

            IntentionSection(title = "THE WILDS") {
                IntentionField(
                    label = "Things to Try",
                    value = uiState.explorations,
                    onValueChange = { viewModel.updateExplorations(it) },
                    placeholder = "e.g. morning pages, fasting till noon, a new route"
                )
            }

            IntentionSection(title = "THE CORE") {
                IntentionField(
                    label = "One-line Week Aim",
                    value = uiState.weeklyAim,
                    onValueChange = { viewModel.updateWeeklyAim(it) },
                    placeholder = "e.g. Ship one feature; finish Book I"
                )
            }

            Button(
                onClick = {
                    viewModel.save()
                    onBack()
                },
                enabled = structuralError == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CathedralGold,
                    contentColor = MonasteryBlack,
                    disabledContainerColor = CathedralGold.copy(alpha = 0.2f),
                    disabledContentColor = Parchment.copy(alpha = 0.3f)
                )
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    when {
                        structuralError != null -> "THE HOURS CANNOT HOLD"
                        uiState.isUpcomingWeek -> "SEAL THE WEEK AHEAD"
                        else -> "SEAL THIS WEEK\u2019S RULE"
                    },
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = "These vows become the architecture of the next seven days: the hours shape the day, and the quests name what fills it. Leave any blank — a vow unspoken is a vow not yet due.",
                style = MaterialTheme.typography.bodySmall,
                color = Parchment.copy(alpha = 0.4f),
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeVowPicker(
    label: String,
    value: String,
    fallback: LocalTime,
    onChange: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val parsed = remember(value) {
        runCatching { LocalTime.parse(value) }.getOrElse { fallback }
    }
    val pickerState = rememberTimePickerState(
        initialHour = parsed.hour,
        initialMinute = parsed.minute,
        is24Hour = true
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Parchment.copy(alpha = 0.4f)
        )
        OutlinedButton(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, CathedralGold.copy(alpha = 0.2f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CathedralGold)
        ) {
            Text(
                text = value.ifBlank { "— declare —" },
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = MonasteryBlack,
            title = {
                Text(
                    label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = CathedralGold,
                    letterSpacing = 2.sp
                )
            },
            text = { TimePicker(state = pickerState) },
            confirmButton = {
                TextButton(onClick = {
                    onChange(String.format("%02d:%02d", pickerState.hour, pickerState.minute))
                    showDialog = false
                }) {
                    Text("SET", color = CathedralGold, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("CANCEL", color = Parchment.copy(alpha = 0.5f))
                }
            }
        )
    }
}

@Composable
fun IntentionPreview(uiState: WeeklyIntentionUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CathedralGold.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, CathedralGold.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "LIVE PREVIEW",
                style = MaterialTheme.typography.labelSmall,
                color = CathedralGold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Structural vows preview
            if (uiState.targetWakeTime.isNotBlank() &&
                uiState.targetSleepTime.isNotBlank() &&
                uiState.dailyFocusHours > 0f
            ) {
                DayScheduler.preview(
                    uiState.targetWakeTime,
                    uiState.targetSleepTime,
                    uiState.dailyFocusHours
                )?.let { (forge, sanctuaryEnd) ->
                    PreviewLine("The Forge", forge)
                    PreviewLine("Sanctuary", sanctuaryEnd)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (uiState.techneFocus.isNotBlank()) {
                PreviewLine("Deep Work", "Build: ${uiState.techneFocus}")
            }
            if (uiState.historiaBook.isNotBlank()) {
                PreviewLine("Archive", "Read from: ${uiState.historiaBook}")
            }
            if (uiState.historiaResearch.isNotBlank()) {
                PreviewLine("Walk", "Turn over: ${uiState.historiaResearch}")
            }
            if (uiState.gymnosFocus.isNotBlank()) {
                PreviewLine("Physical", "Train with emphasis: ${uiState.gymnosFocus}")
            }
            if (uiState.sophiaTheme.isNotBlank()) {
                PreviewLine("Sanctuary", "Evening theme: ${uiState.sophiaTheme}")
            }
            if (uiState.explorations.isNotBlank()) {
                PreviewLine("Walk (no idea set)", "Take one experiment with you: ${uiState.explorations}")
            }
        }
    }
}

@Composable
fun PreviewLine(ritual: String, text: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            "$ritual will say: ",
            style = MaterialTheme.typography.bodySmall,
            color = Parchment.copy(alpha = 0.5f)
        )
        Text(
            "\u201C$text\u201D",
            style = MaterialTheme.typography.bodySmall,
            color = CathedralGold,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun IntentionSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = CathedralGold.copy(alpha = 0.6f),
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
fun IntentionField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Parchment.copy(alpha = 0.4f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Parchment.copy(alpha = 0.2f)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CathedralGold,
                unfocusedBorderColor = CathedralGold.copy(alpha = 0.2f),
                focusedTextColor = Parchment,
                unfocusedTextColor = Parchment
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Serif)
        )
    }
}
