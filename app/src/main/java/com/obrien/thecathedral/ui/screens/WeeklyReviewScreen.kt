package com.obrien.thecathedral.ui.screens

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
import com.obrien.thecathedral.model.WeeklyReview
import com.obrien.thecathedral.ui.theme.CathedralGold
import com.obrien.thecathedral.ui.theme.MonasteryBlack
import com.obrien.thecathedral.ui.theme.Parchment
import com.obrien.thecathedral.viewmodel.PhilosophyViewModel
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "WEEKLY REVIEW",
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "THE EXAMINATION OF FIDELITY",
                style = MaterialTheme.typography.labelMedium,
                color = CathedralGold,
                letterSpacing = 2.sp
            )

            Text(
                text = "“Am I closer to the complete man than I was 7 days ago?”",
                style = MaterialTheme.typography.headlineSmall,
                color = Parchment,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReviewQuestion(
                question = "What was my greatest victory of the week?",
                placeholder = "Describe the moment you mastered your impulse...",
                value = victory,
                onValueChange = { victory = it }
            )

            ReviewQuestion(
                question = "Where did the rule of life bend or break?",
                placeholder = "Identify the weak point in the Cathedral walls...",
                value = failure,
                onValueChange = { failure = it }
            )

            ReviewQuestion(
                question = "What one adjustment will I make for the week ahead?",
                placeholder = "The single stone you will set differently...",
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
                    containerColor = CathedralGold,
                    contentColor = MonasteryBlack
                )
            ) {
                Text("SEAL THE WEEK", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = question,
            style = MaterialTheme.typography.titleSmall,
            color = CathedralGold.copy(alpha = 0.8f),
            fontFamily = FontFamily.Serif
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Parchment.copy(alpha = 0.3f)) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CathedralGold,
                unfocusedBorderColor = CathedralGold.copy(alpha = 0.3f),
                focusedTextColor = Parchment,
                unfocusedTextColor = Parchment
            )
        )
    }
}
