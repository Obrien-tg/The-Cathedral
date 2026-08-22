package com.obrien.thelantern.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.thelantern.data.ScheduleData
import com.obrien.thelantern.viewmodel.HomeworkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkScreen(
    viewModel: HomeworkViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val subjects = ScheduleData.SUBJECTS
    val selectedSubjects = uiState.entries.map { it.subject }.toSet()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HOMEWORK", letterSpacing = 2.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Did school leave you with any homework today?",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(subjects) { subject ->
                    val isSelected = subject in selectedSubjects
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) viewModel.removeSubject(subject)
                            else viewModel.addSubject(subject)
                        },
                        label = { Text(subject, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.heightIn(min = 48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        }
    }
}
