package com.obrien.thecathedral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.thecathedral.data.ScheduleRepository
import com.obrien.thecathedral.model.ExportData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalTime
import javax.inject.Inject

data class SettingsUiState(
    val wakeTime: LocalTime = LocalTime.of(7, 0),
    val notificationLeadTime: Int = 5,
    val theme: String = "dark",
    val fontSize: String = "medium"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _wakeTime = MutableStateFlow(LocalTime.of(7, 0))

    init {
        viewModelScope.launch {
            repository.wakeTime.collect { timeStr ->
                if (timeStr != null) {
                    try {
                        _wakeTime.value = LocalTime.parse(timeStr)
                    } catch (_: Exception) {
                        _wakeTime.value = LocalTime.of(7, 0)
                    }
                }
            }
        }
    }

    val uiState: StateFlow<SettingsUiState> = combine(
        _wakeTime,
        repository.notificationLeadTime,
        repository.theme,
        repository.fontSize
    ) { wake, lead, theme, font ->
        SettingsUiState(wake, lead, theme, font)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setWakeTime(time: LocalTime) {
        _wakeTime.value = time
        viewModelScope.launch {
            repository.setWakeTime(time.toString())
        }
    }

    fun setNotificationLeadTime(minutes: Int) {
        viewModelScope.launch {
            repository.setNotificationLeadTime(minutes)
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            repository.setTheme(theme)
        }
    }

    fun setFontSize(size: String) {
        viewModelScope.launch {
            repository.setFontSize(size)
        }
    }

    suspend fun getExportData(): String {
        val historical = repository.historicalCompletions.first()
        val focusSessions = repository.totalFocusSessions.first()
        val entries = repository.journalEntries.first()
        val weeklyReviews = repository.weeklyReviews.first()

        val data = ExportData(
            wakeTime = _wakeTime.value.toString(),
            historicalCompletions = historical,
            totalFocusSessions = focusSessions,
            journalEntries = entries,
            weeklyReviews = weeklyReviews
        )
        return Json { prettyPrint = true }.encodeToString(data)
    }
}
