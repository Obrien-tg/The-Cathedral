package com.obrien.thecathedral.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.obrien.thecathedral.ui.theme.CathedralGold
import com.obrien.thecathedral.ui.theme.MonasteryBlack
import com.obrien.thecathedral.ui.theme.Parchment
import com.obrien.thecathedral.viewmodel.WeeklyIntentionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyIntentionScreen(
    viewModel: WeeklyIntentionViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "WEEK’S RULE",
                            style = MaterialTheme.typography.titleMedium,
                            letterSpacing = 3.sp,
                            color = CathedralGold
                        )
                        Text(
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

            IntentionSection(title = "THE FORGE (TECHNE)") {
                IntentionField(
                    label = "Active Project / Focus",
                    value = uiState.techneFocus,
                    onValueChange = { viewModel.updateTechneFocus(it) },
                    placeholder = "e.g. Liberty Timeline – event list"
                )
            }

            IntentionSection(title = "THE ARCHIVE (HISTORIA)") {
                IntentionField(
                    label = "Primary Source / Book",
                    value = uiState.historiaBook,
                    onValueChange = { viewModel.updateHistoriaBook(it) },
                    placeholder = "e.g. Thucydides – Book I"
                )
                Spacer(modifier = Modifier.height(12.dp))
                IntentionField(
                    label = "Research Subject",
                    value = uiState.historiaResearch,
                    onValueChange = { viewModel.updateHistoriaResearch(it) },
                    placeholder = "e.g. Pericles and civic speech"
                )
            }

            IntentionSection(title = "THE ARENA (GYMNOS)") {
                IntentionField(
                    label = "Body Focus",
                    value = uiState.gymnosFocus,
                    onValueChange = { viewModel.updateGymnosFocus(it) },
                    placeholder = "e.g. Zone 2 walks / Squat form"
                )
            }

            IntentionSection(title = "THE SANCTUARY (SOPHIA)") {
                IntentionField(
                    label = "Evening Reflection Theme",
                    value = uiState.sophiaTheme,
                    onValueChange = { viewModel.updateSophiaTheme(it) },
                    placeholder = "e.g. Fewer inputs, longer silence"
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CathedralGold,
                    contentColor = MonasteryBlack
                )
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("APPLY TO THIS WEEK", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            
            Text(
                text = "These intentions will reshape the ritual tasks across your schedule for the current week.",
                style = MaterialTheme.typography.bodySmall,
                color = Parchment.copy(alpha = 0.4f),
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
fun IntentionPreview(uiState: com.obrien.thecathedral.viewmodel.WeeklyIntentionUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CathedralGold.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, CathedralGold.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "LIVE PREVIEW",
                style = MaterialTheme.typography.labelSmall,
                color = CathedralGold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            
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
            "“$text”",
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
