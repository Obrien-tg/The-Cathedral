package com.obrien.thecathedral.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.thecathedral.service.ForgeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val _timeRemaining = MutableStateFlow(25 * 60)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _sessionCount = MutableStateFlow(0)
    val sessionCount: StateFlow<Int> = _sessionCount.asStateFlow()

    private var forgeService: ForgeService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ForgeService.ForgeBinder
            forgeService = binder.getService()
            
            viewModelScope.launch {
                forgeService?.timeRemaining?.collect { _timeRemaining.value = it }
            }
            viewModelScope.launch {
                forgeService?.isRunning?.collect { _isRunning.value = it }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            forgeService = null
        }
    }

    init {
        val intent = Intent(application, ForgeService::class.java)
        application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun startFocusTimer() {
        val intent = Intent(application, ForgeService::class.java).apply {
            action = ForgeService.ACTION_START
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            application.startForegroundService(intent)
        } else {
            application.startService(intent)
        }
    }

    fun pauseFocusTimer() {
        val intent = Intent(application, ForgeService::class.java).apply {
            action = ForgeService.ACTION_PAUSE
        }
        application.startService(intent)
    }

    fun resetFocusTimer() {
        val intent = Intent(application, ForgeService::class.java).apply {
            action = ForgeService.ACTION_RESET
        }
        application.startService(intent)
    }

    fun setFocusBreak() {
        val intent = Intent(application, ForgeService::class.java).apply {
            action = ForgeService.ACTION_SET_BREAK
        }
        application.startService(intent)
    }

    override fun onCleared() {
        super.onCleared()
        try {
            application.unbindService(serviceConnection)
        } catch (_: Exception) { }
    }
}
