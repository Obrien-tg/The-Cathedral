package com.example.thecathedral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.thecathedral.data.ScheduleData
import com.example.thecathedral.data.ScheduleRepository
import com.example.thecathedral.model.Alarm
import com.example.thecathedral.model.Pillar
import com.example.thecathedral.model.PillarStatus
import com.example.thecathedral.util.computeStatus
import com.example.thecathedral.util.isActiveAt
import com.example.thecathedral.util.parseTimeRange
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalTime

data class CathedralUiState(
    val currentTime: LocalTime = LocalTime.now(),
    val completedAlarmIds: Set<String> = emptySet(),
    val activePillar: Pillar? = null,
    val nextPillar: Pillar? = null,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val progress: Float = 0f
)

class ScheduleViewModel(private val repository: ScheduleRepository) : ViewModel() {

    private val _currentTime = MutableStateFlow(LocalTime.now())
    val currentTime: StateFlow<LocalTime> = _currentTime.asStateFlow()

    private val _completedAlarmIds = MutableStateFlow<Set<String>>(emptySet())

    init {
        viewModelScope.launch {
            repository.completedAlarms.collect { ids ->
                _completedAlarmIds.value = ids
            }
        }

        viewModelScope.launch {
            while (isActive) {
                _currentTime.value = LocalTime.now()
                delay(60_000L)
            }
        }
    }

    val uiState: StateFlow<CathedralUiState> = combine(
        _currentTime,
        _completedAlarmIds
    ) { time, completedIds ->
        val allAlarms = ScheduleData.pillars.flatMap { it.alarms }
        val total = allAlarms.size
        val completedCount = allAlarms.count { it.id in completedIds }

        val activePillar = ScheduleData.pillars.find { it.isActiveAt(time) }
        val nextPillar = ScheduleData.pillars.find { pillar ->
            val (start, _) = pillar.parseTimeRange() ?: return@find false
            start.isAfter(time)
        }

        CathedralUiState(
            currentTime = time,
            completedAlarmIds = completedIds,
            activePillar = activePillar,
            nextPillar = nextPillar,
            completedCount = completedCount,
            totalCount = total,
            progress = if (total > 0) completedCount.toFloat() / total else 0f
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CathedralUiState()
    )

    fun toggleAlarm(alarmId: String) {
        viewModelScope.launch {
            if (alarmId in _completedAlarmIds.value) {
                repository.markIncomplete(alarmId)
            } else {
                repository.markComplete(alarmId)
            }
        }
    }

    fun getAlarmStatus(alarm: Alarm): PillarStatus {
        val completed = alarm.id in _completedAlarmIds.value
        return alarm.computeStatus(completed, _currentTime.value)
    }

    fun clearAllProgress() {
        viewModelScope.launch {
            repository.clearAllProgress()
        }
    }
}

class ScheduleViewModelFactory(
    private val repository: ScheduleRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            return ScheduleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
