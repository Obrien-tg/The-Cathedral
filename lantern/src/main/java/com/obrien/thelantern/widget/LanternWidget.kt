package com.obrien.thelantern.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.obrien.thelantern.MainActivity
import com.obrien.thelantern.data.ScheduleData
import com.obrien.thelantern.util.isActiveAt
import java.time.LocalTime

class LanternWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        val now = LocalTime.now()
        val activePillar = ScheduleData.pillars.find { it.isActiveAt(now) }
        
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
                .padding(16.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Text(
                text = "LUMI",
                style = TextStyle(
                    color = ColorProvider(Color(0xFFBFA8FF)),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
            if (activePillar != null) {
                Text(
                    text = activePillar.name,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = activePillar.timeRange,
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                        fontSize = 12.sp
                    )
                )
            } else {
                Text(
                    text = "REST",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "The day is done.",
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}
