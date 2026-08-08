package com.example.thecathedral.ui.screens

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
import com.example.thecathedral.data.ScheduleData
import com.example.thecathedral.model.PrimarySource
import com.example.thecathedral.model.PrimarySources
import com.example.thecathedral.ui.theme.CathedralGold
import com.example.thecathedral.ui.theme.MonasteryBlack
import com.example.thecathedral.ui.theme.Parchment
import com.example.thecathedral.ui.theme.RitualSuccess
import com.example.thecathedral.ui.theme.TheCathedralTheme
import com.example.thecathedral.viewmodel.ScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhilosophyScreen(
    viewModel: ScheduleViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val activeIndex = uiState.activeSourceIndex
    val activePage = uiState.activeSourcePage
    val activeSource = PrimarySources.curriculum.getOrNull(activeIndex)
        ?: PrimarySources.curriculum.first()

    var pageInput by remember(activePage) {
        mutableStateOf(if (activePage > 0) activePage.toString() else "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "THE SANCTUARY",
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SacredCard(title = "THE PURPOSE") {
                Text(
                    text = """"${ScheduleData.PURPOSE_STATEMENT}"""",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Parchment,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 26.sp
                )
            }

            SacredCard(title = "THE MANTRA") {
                Text(
                    text = ScheduleData.MANTRA,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CathedralGold.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 24.sp
                )
            }

            SacredCard(title = "EMERGENCY PROTOCOLS") {
                ProtocolItem(
                    title = "The 2-Day Rule",
                    description = "Miss one day: fine. Miss two days: you have broken the contract. Read your purpose aloud and take the smallest possible action to restart."
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProtocolItem(
                    title = "The 5-Minute Rescue",
                    description = "Feel resistance? Commit to just 5 minutes. After 5 minutes, you are free to stop. 90% of the time, you will not want to."
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProtocolItem(
                    title = "The Weekly Review",
                    description = "Every Sunday, ask: 'Am I closer to the complete man than I was 7 days ago?' Then adjust one thing."
                )
            }

            SacredCard(title = "PRIMARY SOURCE CURRICULUM") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CURRENTLY READING",
                            style = MaterialTheme.typography.labelSmall,
                            color = CathedralGold.copy(alpha = 0.6f)
                        )
                        Text(
                            text = activeSource.book,
                            style = MaterialTheme.typography.titleSmall,
                            color = CathedralGold,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "by ${activeSource.pioneer}  •  ${activeSource.field}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Parchment.copy(alpha = 0.6f)
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
                    color = CathedralGold,
                    trackColor = CathedralGold.copy(alpha = 0.1f)
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
                        color = Parchment.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = CathedralGold
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
                        label = { Text("Page", color = CathedralGold.copy(alpha = 0.6f)) },
                        modifier = Modifier.width(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CathedralGold,
                            unfocusedBorderColor = CathedralGold.copy(alpha = 0.3f),
                            focusedTextColor = Parchment,
                            unfocusedTextColor = Parchment
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CathedralGold,
                            contentColor = MonasteryBlack
                        )
                    ) {
                        Text("UPDATE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }

            SacredCard(title = "THE TABLE OF PIONEERS") {
                PrimarySources.curriculum.forEachIndexed { index, source ->
                    val isActive = index == activeIndex
                    SourceRow(
                        source = source,
                        isActive = isActive,
                        onSelect = { viewModel.setActiveSource(index) }
                    )
                    if (index < PrimarySources.curriculum.lastIndex) {
                        HorizontalDivider(
                            color = CathedralGold.copy(alpha = 0.1f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            SacredCard(title = "THE ROTATING CURRICULUM") {
                val days = listOf(
                    "Monday" to "Natural Science — Feynman essays, 3Blue1Brown",
                    "Tuesday" to "Literature — Shakespeare, Poetry",
                    "Wednesday" to "Logic & Rhetoric — Aristotle, Clear thinking",
                    "Thursday" to "Art & Music — Classical pieces, Visual beauty",
                    "Friday" to "Theology & Mythology — Stories that shape souls",
                    "Saturday" to "Freestyle — Follow the rabbit hole",
                    "Sunday" to "Freestyle — Rest or deep dive"
                )
                days.forEach { (day, topic) ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = day,
                            modifier = Modifier.width(80.dp),
                            color = CathedralGold.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = topic,
                            color = Parchment.copy(alpha = 0.7f),
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
    Card(
        colors = CardDefaults.cardColors(containerColor = MonasteryBlack),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            CathedralGold.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = CathedralGold.copy(alpha = 0.6f),
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
            color = CathedralGold,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = description,
            color = Parchment.copy(alpha = 0.6f),
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
            tint = if (isActive) RitualSuccess else CathedralGold.copy(alpha = 0.3f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = source.book,
                color = if (isActive) CathedralGold else Parchment.copy(alpha = 0.7f),
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp
            )
            Text(
                text = "${source.pioneer}  •  ${source.field}",
                color = Parchment.copy(alpha = 0.4f),
                fontSize = 12.sp
            )
        }
        if (isActive) {
            Text(
                text = "READING",
                color = RitualSuccess,
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
    TheCathedralTheme(darkTheme = true) {
        Scaffold(containerColor = MonasteryBlack) { padding ->
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
                        color = Parchment,
                        fontFamily = FontFamily.Serif
                    )
                }
            }
        }
    }
}
