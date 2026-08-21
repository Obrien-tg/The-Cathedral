package com.obrien.thelantern.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.obrien.thelantern.data.ScheduleData
import com.obrien.thelantern.ui.theme.LanternNight
import com.obrien.thelantern.ui.theme.LanternText
import com.obrien.thelantern.ui.theme.LanternSurface
import com.obrien.thelantern.viewmodel.WeeklyIntentionViewModel
import com.obrien.thelantern.viewmodel.WeeklyIntentionUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyIntentionScreen(
    viewModel: WeeklyIntentionViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val primary = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "WEEK’S LIGHT",
                            style = MaterialTheme.typography.titleMedium,
                            letterSpacing = 3.sp,
                            color = primary
                        )
                        Text(
                            "Week of ${uiState.weekStartDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = primary.copy(alpha = 0.5f)
                        )
                    }
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
                actions = {
                    IconButton(onClick = { viewModel.clear() }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (uiState.isAnythingSet()) {
                IntentionPreview(uiState)
            }

            IntentionSection(title = "ACADEMIC FOCUS") {
                SubjectDropdown(
                    selectedSubject = uiState.subjectFocus,
                    onSubjectSelected = { viewModel.updateSubjectFocus(it) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                IntentionField(
                    label = "One-line Aim (e.g. fractions word problems)",
                    value = uiState.weekAim,
                    onValueChange = { viewModel.updateWeekAim(it) },
                    placeholder = "What is the main goal?"
                )
            }

            IntentionSection(title = "READING") {
                IntentionField(
                    label = "Book Title",
                    value = uiState.book,
                    onValueChange = { viewModel.updateBook(it) },
                    placeholder = "What are you reading this week?"
                )
            }

            IntentionSection(title = "BODY") {
                IntentionField(
                    label = "Sport / Movement Focus",
                    value = uiState.bodyFocus,
                    onValueChange = { viewModel.updateBodyFocus(it) },
                    placeholder = "e.g. Tennis practice / Daily walk"
                )
            }

            IntentionSection(title = "CHARACTER") {
                IntentionField(
                    label = "Character Aim (optional)",
                    value = uiState.characterAim,
                    onValueChange = { viewModel.updateCharacterAim(it) },
                    placeholder = "e.g. Speak up, patience, honesty..."
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
                    containerColor = primary,
                    contentColor = LanternNight
                )
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("SET THIS WEEK'S LIGHT", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            
            Text(
                text = "These intentions will light up your schedule for the current week.",
                style = MaterialTheme.typography.bodySmall,
                color = LanternText.copy(alpha = 0.4f),
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDropdown(
    selectedSubject: String,
    onSubjectSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val subjects = ScheduleData.SUBJECTS
    val primary = MaterialTheme.colorScheme.primary

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Subject Focus",
            style = MaterialTheme.typography.labelSmall,
            color = LanternText.copy(alpha = 0.4f)
        )
        Box {
            OutlinedTextField(
                value = selectedSubject,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select a subject", color = LanternText.copy(alpha = 0.2f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = primary)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primary,
                    unfocusedBorderColor = primary.copy(alpha = 0.2f),
                    focusedTextColor = LanternText,
                    unfocusedTextColor = LanternText
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = false // Disable direct editing
            )
            // Overlay clickable to handle the whole area including disabled text field
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(LanternSurface)
            ) {
                subjects.forEach { subject ->
                    DropdownMenuItem(
                        text = { Text(subject, color = LanternText) },
                        onClick = {
                            onSubjectSelected(subject)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun IntentionPreview(uiState: WeeklyIntentionUiState) {
    val primary = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = primary.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, primary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "THIS WEEK'S LIGHT",
                style = MaterialTheme.typography.labelSmall,
                color = primary,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            if (uiState.subjectFocus.isNotBlank()) {
                val text = if (uiState.weekAim.isNotBlank()) "${uiState.subjectFocus}: ${uiState.weekAim}" else uiState.subjectFocus
                PreviewLine("Study Forge", text)
            }
            if (uiState.book.isNotBlank()) {
                PreviewLine("Read", uiState.book)
            }
            if (uiState.bodyFocus.isNotBlank()) {
                PreviewLine("Move", uiState.bodyFocus)
            }
            if (uiState.characterAim.isNotBlank()) {
                PreviewLine("Character", uiState.characterAim)
            }
        }
    }
}

@Composable
fun PreviewLine(ritual: String, text: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            "$ritual: ",
            style = MaterialTheme.typography.bodySmall,
            color = LanternText.copy(alpha = 0.5f)
        )
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun IntentionSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = primary.copy(alpha = 0.6f),
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
    val primary = MaterialTheme.colorScheme.primary
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = LanternText.copy(alpha = 0.4f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = LanternText.copy(alpha = 0.2f)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primary,
                unfocusedBorderColor = primary.copy(alpha = 0.2f),
                focusedTextColor = LanternText,
                unfocusedTextColor = LanternText
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Serif)
        )
    }
}
