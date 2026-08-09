package com.obrien.thecathedral.service

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.obrien.thecathedral.MainActivity
import com.obrien.thecathedral.R
import com.obrien.thecathedral.data.ScheduleRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@AndroidEntryPoint
class ForgeService : Service() {

    @Inject
    lateinit var repository: ScheduleRepository

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null

    private val _timeRemaining = MutableStateFlow(25 * 60)
    val timeRemaining: StateFlow<Int> = _timeRemaining

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val binder = ForgeBinder()

    companion object {
        const val CHANNEL_ID = "forge_channel"
        const val NOTIFICATION_ID = 1001
        
        const val ACTION_START = "START"
        const val ACTION_PAUSE = "PAUSE"
        const val ACTION_RESET = "RESET"
        const val ACTION_SET_BREAK = "SET_BREAK"
    }

    inner class ForgeBinder : Binder() {
        fun getService(): ForgeService = this@ForgeService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTimer()
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESET -> resetTimer()
            ACTION_SET_BREAK -> setBreak()
        }
        return START_NOT_STICKY
    }

    private fun startTimer() {
        if (_isRunning.value) return
        _isRunning.value = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID, 
                buildNotification(), 
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) 
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC 
                else 0
            )
        } else {
            startForeground(NOTIFICATION_ID, buildNotification())
        }
        
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (_isRunning.value && _timeRemaining.value > 0) {
                delay(1000L)
                _timeRemaining.value--
                updateNotification()
            }
            if (_timeRemaining.value == 0) {
                _isRunning.value = false
                repository.incrementFocusSessions()
                updateNotification("Forge Ritual Complete", "The iron has been tempered.")
                stopForeground(STOP_FOREGROUND_DETACH)
            }
        }
    }

    private fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        updateNotification()
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    private fun resetTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        _timeRemaining.value = 25 * 60
        stopSelf()
    }

    private fun setBreak() {
        _isRunning.value = false
        timerJob?.cancel()
        _timeRemaining.value = 5 * 60
        updateNotification()
    }

    private fun updateNotification(title: String? = null, content: String? = null) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, buildNotification(title, content))
    }

    private fun buildNotification(title: String? = null, content: String? = null): Notification {
        val minutes = _timeRemaining.value / 60
        val seconds = _timeRemaining.value % 60
        val timeStr = String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title ?: "The Forge Active")
            .setContentText(content ?: "Deep Work in progress: $timeStr")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW) // Avoid noisy updates
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "The Forge", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
