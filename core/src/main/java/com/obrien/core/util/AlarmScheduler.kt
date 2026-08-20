package com.obrien.core.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.obrien.core.model.Pillar
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

    fun scheduleRitualAlarms(
        pillars: List<Pillar>,
        receiverClass: Class<out BroadcastReceiver>,
        wakeTime: LocalTime = LocalTime.of(7, 0),
        baseWake: LocalTime = LocalTime.of(7, 0)
    ) {
        val offset = Duration.between(baseWake, wakeTime)

        pillars.forEach { pillar ->
            pillar.alarms.forEach { alarm ->
                val shiftedTime = alarm.time.plus(offset)
                scheduleSingleAlarm(
                    id = alarm.id,
                    title = alarm.name,
                    time = shiftedTime,
                    receiverClass = receiverClass
                )
            }
        }
    }

    private fun scheduleSingleAlarm(
        id: String,
        title: String,
        time: LocalTime,
        receiverClass: Class<out BroadcastReceiver>
    ) {
        var trigger = LocalDate.now()
            .atTime(time)
            .atZone(ZoneId.systemDefault())

        if (trigger.toInstant().toEpochMilli() <= System.currentTimeMillis()) {
            trigger = trigger.plusDays(1)
        }

        val intent = Intent(context, receiverClass).apply {
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
            e.printStackTrace()
        }
    }
}
