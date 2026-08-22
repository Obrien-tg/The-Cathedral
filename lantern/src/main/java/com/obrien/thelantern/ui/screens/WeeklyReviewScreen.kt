package com.obrien.thelantern.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.core.model.WeeklyReview
import com.obrien.thelantern.ui.theme.LanternNight
import com.obrien.thelantern.ui.theme.LanternText
import com.obrien.thelantern.viewmodel.PhilosophyViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReviewScreen(
    viewModel: PhilosophyViewModel,
    onBack: () -> Unit = {}
) {
    val today = LocalDate.now().toString()
    var victory by remember { mutableStateOf("") }
    var failure by remember { mutableStateOf("") }
    var adjustment by remember { mutableStateOf("") }
    val primary = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "WEEKLY REVIEW",
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "MY WEEKLY REVIEW",
                style = MaterialTheme.typography.labelMedium,
                color = primary,
                letterSpacing = 2.sp
            )

            Text(
                text = "“Am I growing into the person I want to be?”",
                style = MaterialTheme.typography.headlineSmall,
                color = LanternText,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReviewQuestion(
                question = "What was my greatest win this week?",
                placeholder = "Describe a moment you are proud of...",
                value = victory,
                onValueChange = { victory = it }
            )

            ReviewQuestion(
                question = "Where did I struggle to keep my promise?",
                placeholder = "Where could it have gone better?...",
                value = failure,
                onValueChange = { failure = it }
            )

            ReviewQuestion(
                question = "What is one thing I'll do differently next week?",
                placeholder = "One small change to try...",
                value = adjustment,
                onValueChange = { adjustment = it }
            )

            Button(
                onClick = {
                    viewModel.saveWeeklyReview(
                        WeeklyReview(
                            date = today,
                            victory = victory,
                            failure = failure,
                            adjustment = adjustment
                        )
                    )
                    onBack()
                },
                enabled = victory.isNotBlank() && failure.isNotBlank() && adjustment.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = LanternNight
                )
            ) {
                Text("DONE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun ReviewQuestion(
    question: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = question,
            style = MaterialTheme.typography.titleSmall,
            color = primary.copy(alpha = 0.8f),
            fontFamily = FontFamily.Serif
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = LanternText.copy(alpha = 0.3f)) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primary,
                unfocusedBorderColor = primary.copy(alpha = 0.3f),
                focusedTextColor = LanternText,
                unfocusedTextColor = LanternText
            )
        )
    }
}
