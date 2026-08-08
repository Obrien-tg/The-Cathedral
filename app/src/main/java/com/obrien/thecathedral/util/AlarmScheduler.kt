package com.obrien.thecathedral.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.obrien.thecathedral.data.ScheduleData
import com.obrien.thecathedral.receiver.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleRitualAlarms() {
        ScheduleData.pillars.forEach { pillar ->
            val regex = """(\d{1,2}):(\d{2})""".toRegex()
            val match = regex.find(pillar.timeRange) ?: return@forEach
            val (hour, minute) = match.destructured
            
            val ritualTime = LocalTime.of(hour.toInt(), minute.toInt())
            val calendar = LocalDate.now().atTime(ritualTime).atZone(ZoneId.systemDefault())
            
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("ritual_title", pillar.name)
                putExtra("ritual_message", "Your next ritual begins now: ${pillar.name}")
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                pillar.name.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // If time has already passed today, don't schedule or schedule for tomorrow
            if (calendar.toInstant().toEpochMilli() > System.currentTimeMillis()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.toInstant().toEpochMilli(),
                    pendingIntent
                )
            }
        }
    }
}
