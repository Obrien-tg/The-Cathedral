package com.obrien.thelantern.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obrien.thelantern.ui.theme.LanternGold
import com.obrien.thelantern.ui.theme.LanternNight
import java.time.LocalDate

@Composable
fun FidelityHeatmap(
    completionHistory: Map<String, Int>,
    totalRituals: Int,
    modifier: Modifier = Modifier,
    weeksToShow: Int = 12
) {
    val today = LocalDate.now()
    // Find the Monday of the starting week
    val startDay = today.minusWeeks(weeksToShow.toLong() - 1).minusDays(today.dayOfWeek.value.toLong() - 1)
    
    Column(
        modifier = modifier
            .background(LanternNight, RoundedCornerShape(12.dp))
            .border(1.dp, LanternGold.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "HEATMAP OF FIDELITY",
            style = MaterialTheme.typography.labelSmall,
            color = LanternGold.copy(alpha = 0.6f),
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(weeksToShow) { weekIdx ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(7) { dayIdx ->
                            val date = startDay.plusWeeks(weekIdx.toLong()).plusDays(dayIdx.toLong())
                            val count = completionHistory[date.toString()] ?: 0
                            val intensity = if (totalRituals > 0) count.toFloat() / totalRituals else 0f
                            
                            HeatmapCell(intensity = intensity, isToday = date == today)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("LESS", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.3f))
            HeatmapCell(intensity = 0f, isToday = false)
            HeatmapCell(intensity = 0.3f, isToday = false)
            HeatmapCell(intensity = 0.6f, isToday = false)
            HeatmapCell(intensity = 1f, isToday = false)
            Text("MORE", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.3f))
        }
    }
}

@Composable
fun HeatmapCell(
    intensity: Float,
    isToday: Boolean
) {
    val color = when {
        intensity <= 0f -> Color.White.copy(alpha = 0.05f)
        intensity < 0.3f -> LanternGold.copy(alpha = 0.2f)
        intensity < 0.6f -> LanternGold.copy(alpha = 0.5f)
        intensity < 0.9f -> LanternGold.copy(alpha = 0.8f)
        else -> LanternGold
    }
    
    Box(
        modifier = Modifier
            .size(10.dp)
            .background(color, RoundedCornerShape(2.dp))
            .then(
                if (isToday) Modifier.border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                else Modifier
            )
    )
}
