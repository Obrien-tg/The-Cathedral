package com.obrien.core.focus

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.obrien.core.data.ScheduleRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@AndroidEntryPoint
class FocusService : Service() {

    @Inject
    lateinit var repository: ScheduleRepository

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null

    private val _timeRemaining = MutableStateFlow(25 * 60)
    val timeRemaining: StateFlow<Int> = _timeRemaining

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _currentKind = MutableStateFlow(FocusKind.DEEP_WORK)
    val currentKind: StateFlow<FocusKind> = _currentKind

    private val _currentTarget = MutableStateFlow("")
    val currentTarget: StateFlow<String> = _currentTarget

    private val binder = FocusBinder()

    companion object {
        private const val CHANNEL_ID = "focus_channel"
        private const val NOTIFICATION_ID = 2002
        
        const val ACTION_START = "START"
        const val ACTION_PAUSE = "PAUSE"
        const val ACTION_RESET = "RESET"
        const val ACTION_SET_DURATION = "SET_DURATION"
        
        const val EXTRA_MINUTES = "MINUTES"
        const val EXTRA_KIND = "KIND"
        const val EXTRA_TARGET = "TARGET"
        const val EXTRA_MAIN_ACTIVITY = "MAIN_ACTIVITY_CLASS"
    }

    inner class FocusBinder : Binder() {
        fun getService(): FocusService = this@FocusService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val kindStr = intent.getStringExtra(EXTRA_KIND)
                val target = intent.getStringExtra(EXTRA_TARGET) ?: ""
                val kind = if (kindStr == FocusKind.MINDFULNESS.name) FocusKind.MINDFULNESS else FocusKind.DEEP_WORK
                val mainActivityClass = intent.getStringExtra(EXTRA_MAIN_ACTIVITY)
                startTimer(kind, target, mainActivityClass)
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESET -> resetTimer()
            ACTION_SET_DURATION -> {
                val mins = intent.getIntExtra(EXTRA_MINUTES, 25)
                _timeRemaining.value = mins * 60
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer(kind: FocusKind, target: String, mainActivityClass: String?) {
        if (_isRunning.value) return
        _isRunning.value = true
        _currentKind.value = kind
        _currentTarget.value = target

        val notification = buildNotification(mainActivityClass)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID, 
                notification, 
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) 
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC 
                else 0
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (_isRunning.value && _timeRemaining.value > 0) {
                delay(1000L)
                _timeRemaining.value--
                updateNotification(mainActivityClass)
            }
            if (_timeRemaining.value == 0) {
                _isRunning.value = false
                if (kind == FocusKind.DEEP_WORK) {
                    repository.incrementFocusSessions()
                }
                updateNotification(mainActivityClass, "Session Complete", "The ritual is sealed.")
                stopForeground(STOP_FOREGROUND_DETACH)
            }
        }
    }

    private fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    private fun resetTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        _timeRemaining.value = 25 * 60
        stopSelf()
    }

    private fun updateNotification(mainActivityClass: String?, title: String? = null, content: String? = null) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, buildNotification(mainActivityClass, title, content))
    }

    private fun buildNotification(mainActivityClass: String?, title: String? = null, content: String? = null): Notification {
        val minutes = _timeRemaining.value / 60
        val seconds = _timeRemaining.value % 60
        val timeStr = String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds)

        val intent = if (mainActivityClass != null) {
            try {
                Intent(this, Class.forName(mainActivityClass))
            } catch (e: Exception) {
                null
            }
        } else null

        val pendingIntent = if (intent != null) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else null

        val defaultTitle = if (_currentKind.value == FocusKind.DEEP_WORK) "Deep Work" else "Mindfulness"
        val defaultContent = if (_currentKind.value == FocusKind.DEEP_WORK && _currentTarget.value.isNotBlank()) 
            "${_currentTarget.value}: $timeStr" else "$timeStr remaining"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title ?: defaultTitle)
            .setContentText(content ?: defaultContent)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Focus Mode", NotificationManager.IMPORTANCE_LOW
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
