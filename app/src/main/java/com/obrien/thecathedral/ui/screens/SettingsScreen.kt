package com.obrien.thecathedral.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.thecathedral.ui.theme.CathedralGold
import com.obrien.thecathedral.ui.theme.MonasteryBlack
import com.obrien.thecathedral.ui.theme.Parchment
import com.obrien.thecathedral.viewmodel.ScheduleViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ScheduleViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = uiState.wakeTime.hour,
        initialMinute = uiState.wakeTime.minute
    )

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setWakeTime(LocalTime.of(timePickerState.hour, timePickerState.minute))
                    showTimePicker = false
                }) { Text("SET", color = CathedralGold) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("CANCEL", color = CathedralGold.copy(alpha = 0.6f)) }
            },
            containerColor = MonasteryBlack,
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = MonasteryBlack,
                            selectorColor = CathedralGold,
                            containerColor = MonasteryBlack,
                            periodSelectorSelectedContainerColor = CathedralGold,
                            periodSelectorUnselectedContainerColor = MonasteryBlack,
                            periodSelectorSelectedContentColor = MonasteryBlack,
                            periodSelectorUnselectedContentColor = CathedralGold,
                            clockDialUnselectedContentColor = CathedralGold.copy(alpha = 0.5f),
                            clockDialSelectedContentColor = MonasteryBlack
                        )
                    )
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "PREFERENCES",
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SettingsSection(title = "CHRONOS") {
                SettingRow(
                    label = "Wake Time",
                    value = uiState.wakeTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    onClick = { showTimePicker = true }
                )
                
                SettingRow(
                    label = "Notification Lead",
                    value = "${uiState.notificationLeadTime} min",
                    onClick = { /* Simple cycles or dialog */ }
                )
            }

            SettingsSection(title = "AESTHETICS") {
                SettingRow(
                    label = "Theme",
                    value = uiState.theme.uppercase(),
                    onClick = { 
                        val next = if (uiState.theme == "dark") "light" else "dark"
                        viewModel.setTheme(next)
                    }
                )
                
                SettingRow(
                    label = "Font Size",
                    value = uiState.fontSize.uppercase(),
                    onClick = {
                        val sizes = listOf("small", "medium", "large")
                        val next = sizes[(sizes.indexOf(uiState.fontSize) + 1) % sizes.size]
                        viewModel.setFontSize(next)
                    }
                )
            }

            SettingsSection(title = "THE ARCHIVE") {
                val context = LocalContext.current
                Button(
                    onClick = { 
                        val json = viewModel.getExportData()
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "The Cathedral Codex - Data Export")
                            putExtra(android.content.Intent.EXTRA_TEXT, json)
                        }
                        context.startActivity(android.content.Intent.createChooser(intent, "Export Data"))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CathedralGold.copy(alpha = 0.1f),
                        contentColor = CathedralGold
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("EXPORT HISTORY", fontWeight = FontWeight.Bold)
                }
                
                Text(
                    text = "Export your journal and ritual history as a JSON file for your own records.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Parchment.copy(alpha = 0.4f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "The Cathedral Codex v1.2",
                style = MaterialTheme.typography.labelSmall,
                color = CathedralGold.copy(alpha = 0.2f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = CathedralGold.copy(alpha = 0.5f),
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MonasteryBlack),
            border = androidx.compose.foundation.BorderStroke(1.dp, CathedralGold.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = Parchment)
            Text(text = value, color = CathedralGold, fontWeight = FontWeight.Bold)
        }
    }
}
