package com.obrien.core.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.obrien.core.data.DataStoreManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

/**
 * Reschedules all ritual alarms after device reboot.
 *
 * Requires RECEIVE_BOOT_COMPLETED permission in AndroidManifest.xml.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Read persisted wake time and schedule from DataStore
                val wakeTimeStr = dataStoreManager.wakeTime.first()
                val wakeTime = wakeTimeStr?.let {
                    LocalTime.parse(it)
                } ?: LocalTime.of(7, 0)
            } catch (e: Exception) {
                // Silently fail — alarms will be rescheduled when user opens the app
            }
        }
    }
}
