package com.obrien.thecathedral.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.obrien.thecathedral.data.ScheduleData
import com.obrien.thecathedral.notifications.PillarReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
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

    /**
     * Schedule every individual alarm, shifted by the user's wake time.
     * Default wake time is 07:00.
     */
    fun scheduleRitualAlarms(wakeTime: LocalTime = LocalTime.of(7, 0)) {
        val baseWake = LocalTime.of(7, 0)
        val offset = Duration.between(baseWake, wakeTime)

        ScheduleData.pillars.forEach { pillar ->
            pillar.alarms.forEach { alarm ->
                val shiftedTime = alarm.time.plus(offset)
                scheduleSingleAlarm(
                    id = alarm.id,
                    title = alarm.name,
                    time = shiftedTime
                )
            }
        }
    }

    private fun scheduleSingleAlarm(id: String, title: String, time: LocalTime) {
        var trigger = LocalDate.now()
            .atTime(time)
            .atZone(ZoneId.systemDefault())

        // If the time has already passed today, schedule for tomorrow
        if (trigger.toInstant().toEpochMilli() <= System.currentTimeMillis()) {
            trigger = trigger.plusDays(1)
        }

        val intent = Intent(context, PillarReceiver::class.java).apply {
            putExtra("alarm_id", id)
            putExtra("ritual_title", title)
            putExtra("ritual_message", "Ritual begins: $title")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        trigger.toInstant().toEpochMilli(),
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    trigger.toInstant().toEpochMilli(),
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Exact alarms not permitted – silently ignore for now
            e.printStackTrace()
        }
    }
}
