package com.obrien.thelantern.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.thelantern.data.ScheduleData
import com.obrien.thelantern.model.PrimarySource
import com.obrien.thelantern.model.PrimarySources
import com.obrien.thelantern.ui.theme.LanternNight
import com.obrien.thelantern.ui.theme.LanternText
import com.obrien.thelantern.ui.theme.LanternMiss
import com.obrien.thelantern.ui.theme.LanternSuccess
import com.obrien.thelantern.ui.theme.LumiTheme
import com.obrien.thelantern.viewmodel.PhilosophyViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhilosophyScreen(
    viewModel: PhilosophyViewModel,
    onBack: () -> Unit = {},
    onWeeklyReview: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val activeIndex = uiState.activeSourceIndex
    val activePage = uiState.activeSourcePage
    val primary = MaterialTheme.colorScheme.primary
    
    val activeSource = PrimarySources.curriculum.getOrNull(activeIndex)
        ?: PrimarySources.curriculum.first()

    var pageInput by remember(activePage) {
        mutableStateOf(if (activePage > 0) activePage.toString() else "")
    }

    val isPageValid = pageInput.toIntOrNull()?.let { it in 0..activeSource.totalPages } ?: false
    val showError = pageInput.isNotBlank() && !isPageValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "MY WORDS",
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SacredCard(title = "THE PURPOSE") {
                Text(
                    text = """"${ScheduleData.PURPOSE_STATEMENT}"""",
                    style = MaterialTheme.typography.bodyLarge,
                    color = LanternText,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 26.sp
                )
            }

            SacredCard(title = "MY WORDS") {
                Text(
                    text = ScheduleData.MANTRA,
                    style = MaterialTheme.typography.bodyMedium,
                    color = primary.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 24.sp
                )
            }

            SacredCard(title = "WHEN IT'S HARD") {
                ProtocolItem(
                    title = "The 2-Day Rule",
                    description = "Miss one day: fine. Miss two days: the light has flickered. Read your purpose aloud and take the smallest possible action to light it again."
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProtocolItem(
                    title = "The 5-Minute Rescue",
                    description = "Feel resistance? Commit to just 5 minutes. After 5 minutes, you are free to stop. Most of the time, you will want to keep going."
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProtocolItem(
                    title = "The Weekly Review",
                    description = "Every Sunday, ask: 'Am I growing into the person I want to be?' Then adjust one thing for next week."
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onWeeklyReview,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, primary.copy(alpha = 0.3f))
                ) {
                    Text("START REVIEW", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }

            SacredCard(title = "CURRENT READING LIST") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CURRENTLY READING",
                            style = MaterialTheme.typography.labelSmall,
                            color = primary.copy(alpha = 0.6f)
                        )
                        Text(
                            text = activeSource.book,
                            style = MaterialTheme.typography.titleSmall,
                            color = primary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "by ${activeSource.pioneer}  •  ${activeSource.field}",
                            style = MaterialTheme.typography.bodySmall,
                            color = LanternText.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val progress = if (activeSource.totalPages > 0)
                    activePage.toFloat() / activeSource.totalPages else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = primary,
                    trackColor = primary.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$activePage / ${activeSource.totalPages} pages",
                        style = MaterialTheme.typography.labelSmall,
                        color = LanternText.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = pageInput,
                        onValueChange = { pageInput = it.filter { c -> c.isDigit() } },
                        label = { Text("Page", color = primary.copy(alpha = 0.6f)) },
                        isError = showError,
                        supportingText = {
                            if (showError) {
                                Text("Enter 0-${activeSource.totalPages}", color = LanternMiss)
                            }
                        },
                        modifier = Modifier.width(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primary,
                            unfocusedBorderColor = primary.copy(alpha = 0.3f),
                            focusedTextColor = LanternText,
                            unfocusedTextColor = LanternText,
                            errorBorderColor = LanternMiss
                        ),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            pageInput.toIntOrNull()?.let { page ->
                                if (page in 0..activeSource.totalPages) {
                                    viewModel.setActiveSourcePage(page)
                                }
                            }
                        },
                        enabled = isPageValid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primary,
                            contentColor = LanternNight
                        )
                    ) {
                        Text("UPDATE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }

            SacredCard(title = "THE READING PATH") {
                PrimarySources.curriculum.forEachIndexed { index, source ->
                    val isActive = index == activeIndex
                    SourceRow(
                        source = source,
                        isActive = isActive,
                        onSelect = { viewModel.setActiveSource(index) }
                    )
                    if (index < PrimarySources.curriculum.lastIndex) {
                        HorizontalDivider(
                            color = primary.copy(alpha = 0.1f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            SacredCard(title = "THE WEEKLY SUBJECTS") {
                val days = listOf(
                    "Mathematics" to "Numbers, patterns, and logic",
                    "Languages" to "English & Afrikaans stories and words",
                    "Sciences" to "Natural Sciences & Social Sciences",
                    "EMS" to "Economic and Management Sciences",
                    "Technology" to "Building and creating",
                    "Life Orientation" to "Care for self and others",
                    "Creative Arts" to "Visual beauty and performance"
                )
                days.forEach { (day, topic) ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = day,
                            modifier = Modifier.width(80.dp),
                            color = primary.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = topic,
                            color = LanternText.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SacredCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    Card(
        colors = CardDefaults.cardColors(containerColor = LanternNight),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            primary.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = primary.copy(alpha = 0.6f),
                letterSpacing = 3.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun ProtocolItem(title: String, description: String) {
    Column {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = description,
            color = LanternText.copy(alpha = 0.6f),
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontFamily = FontFamily.Serif
        )
    }
}

@Composable
fun SourceRow(
    source: PrimarySource,
    isActive: Boolean,
    onSelect: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = if (isActive)
                Icons.Filled.CheckCircle
            else
                Icons.Outlined.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isActive) LanternSuccess else primary.copy(alpha = 0.3f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = source.book,
                color = if (isActive) primary else LanternText.copy(alpha = 0.7f),
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp
            )
            Text(
                text = "${source.pioneer}  •  ${source.field}",
                color = LanternText.copy(alpha = 0.4f),
                fontSize = 12.sp
            )
        }
        if (isActive) {
            Text(
                text = "READING",
                color = LanternSuccess,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Preview
@Composable
fun PhilosophyScreenPreview() {
    LumiTheme(darkTheme = true) {
        Scaffold(containerColor = LanternNight) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SacredCard(title = "THE PURPOSE") {
                    Text(
                        text = ScheduleData.PURPOSE_STATEMENT,
                        color = LanternText,
                        fontFamily = FontFamily.Serif
                    )
                }
            }
        }
    }
}
