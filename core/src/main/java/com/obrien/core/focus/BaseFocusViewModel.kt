package com.obrien.core.focus

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.core.data.ScheduleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseFocusViewModel @Inject constructor(
    private val application: Application,
    protected val repository: ScheduleRepository
) : ViewModel() {

    private val _timeRemaining = MutableStateFlow(25 * 60)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _currentKind = MutableStateFlow(FocusKind.DEEP_WORK)
    val currentKind: StateFlow<FocusKind> = _currentKind

    private val _currentTarget = MutableStateFlow("")
    val currentTarget: StateFlow<String> = _currentTarget

    private val _sessionCount = MutableStateFlow(0)
    val sessionCount: StateFlow<Int> = _sessionCount.asStateFlow()

    private var focusService: FocusService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as FocusService.FocusBinder
            focusService = binder.getService()
            
            viewModelScope.launch {
                focusService?.timeRemaining?.collect { _timeRemaining.value = it }
            }
            viewModelScope.launch {
                focusService?.isRunning?.collect { _isRunning.value = it }
            }
            viewModelScope.launch {
                focusService?.currentKind?.collect { _currentKind.value = it }
            }
            viewModelScope.launch {
                focusService?.currentTarget?.collect { _currentTarget.value = it }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            focusService = null
        }
    }

    init {
        val intent = Intent(application, FocusService::class.java)
        application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        
        viewModelScope.launch {
            repository.totalFocusSessions.collect { _sessionCount.value = it }
        }
    }

    fun startFocus(kind: FocusKind, target: String, durationMins: Int, mainActivityClass: String) {
        val intent = Intent(application, FocusService::class.java).apply {
            action = FocusService.ACTION_START
            putExtra(FocusService.EXTRA_KIND, kind.name)
            putExtra(FocusService.EXTRA_TARGET, target)
            putExtra(FocusService.EXTRA_MINUTES, durationMins)
            putExtra(FocusService.EXTRA_MAIN_ACTIVITY, mainActivityClass)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            application.startForegroundService(intent)
        } else {
            application.startService(intent)
        }
    }

    fun pauseFocus() {
        val intent = Intent(application, FocusService::class.java).apply {
            action = FocusService.ACTION_PAUSE
        }
        application.startService(intent)
    }

    fun resetFocus() {
        val intent = Intent(application, FocusService::class.java).apply {
            action = FocusService.ACTION_RESET
        }
        application.startService(intent)
    }

    fun setDuration(minutes: Int) {
        val intent = Intent(application, FocusService::class.java).apply {
            action = FocusService.ACTION_SET_DURATION
            putExtra(FocusService.EXTRA_MINUTES, minutes)
        }
        application.startService(intent)
        _timeRemaining.value = minutes * 60
    }

    override fun onCleared() {
        super.onCleared()
        try {
            application.unbindService(serviceConnection)
        } catch (_: Exception) { }
    }
}
