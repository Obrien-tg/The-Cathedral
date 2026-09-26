package com.obrien.thelantern.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.core.data.DataStoreManager
import kotlinx.coroutines.launch

@Composable
fun TutorialScreen(
    dataStoreManager: DataStoreManager,
    onComplete: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    
    // We store these locally and save at the end
    val selectedColors = remember { 
        mutableStateMapOf<String, String>().apply {
            listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY").forEach { day ->
                this[day] = getDefaultColorForDay(day)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "tutorial_content"
                ) { page ->
                    when (page) {
                        0 -> WelcomePage()
                        1 -> DayPartsPage()
                        2 -> ColorPickerPage(selectedColors) { day, color ->
                            selectedColors[day] = color
                        }
                        3 -> HomeworkPage()
                        4 -> FinalPage()
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentPage < 4) {
                    TextButton(onClick = onComplete) {
                        Text("Skip", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    }
                    Button(
                        onClick = { currentPage++ },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Next")
                    }
                } else {
                    Button(
                        onClick = {
                            scope.launch {
                                selectedColors.forEach { (day, color) ->
                                    dataStoreManager.saveDayColor(day, color)
                                }
                                dataStoreManager.setTutorialSeen()
                                onComplete()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Start My Day", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomePage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Hi, I'm Lumi",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "I help you move through your day — one small step at a time.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Serif
        )
    }
}

@Composable
fun DayPartsPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Your Day Has Parts",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        listOf("Morning", "School", "Study", "Move", "Rest").forEach { part ->
            Card(
                modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(0.7f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Text(
                    part,
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "I keep them gentle.",
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
fun ColorPickerPage(
    selectedColors: Map<String, String>,
    onColorSelected: (String, String) -> Unit
) {
    val days = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
    var currentDayIndex by remember { mutableIntStateOf(0) }
    val colors = listOf(
        "E85D75", "8B5CF6", "3B82F6", "10B981",
        "F59E0B", "EAB308", "A855F7", "22C55E",
        "F06292", "4FC3F7", "4DB6AC", "BA68C8",
        "FF8A65", "A1887F", "90A4AE", "9575CD"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Pick Your Colors",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEachIndexed { index, day ->
                val color = Color(android.graphics.Color.parseColor("#${selectedColors[day]}"))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { currentDayIndex = index }
                            .then(if (currentDayIndex == index) Modifier.border(2.dp, MaterialTheme.colorScheme.onBackground, CircleShape) else Modifier)
                    )
                    Text(day.take(1), fontSize = 10.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("What color feels like ${days[currentDayIndex].lowercase().replaceFirstChar { it.uppercase() }}?")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(colors) { hex ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(Color(android.graphics.Color.parseColor("#$hex")))
                        .clickable { onColorSelected(days[currentDayIndex], hex) }
                )
            }
        }
    }
}

@Composable
fun HomeworkPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Homework Check-In",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "After school, I'll ask: 'Any homework today?'\nYou tap your subjects. Easy.",
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(32.dp))
        // Simplified visual grid
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Math", "English", "Science").forEach {
                FilterChip(
                    selected = it == "Math",
                    onClick = {},
                    label = { Text(it) }
                )
            }
        }
    }
}

@Composable
fun FinalPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "That's Everything",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "You can always find me in your pocket.\nLet's begin.",
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Serif
        )
    }
}

private fun getDefaultColorForDay(day: String): String = when (day.uppercase()) {
    "MONDAY" -> "E85D75"
    "TUESDAY" -> "8B5CF6"
    "WEDNESDAY" -> "3B82F6"
    "THURSDAY" -> "10B981"
    "FRIDAY" -> "F59E0B"
    "SATURDAY" -> "EAB308"
    "SUNDAY" -> "A855F7"
    else -> "E85D75"
}
