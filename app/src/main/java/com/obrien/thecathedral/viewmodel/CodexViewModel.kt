package com.obrien.thecathedral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.thecathedral.data.ScheduleRepository
import com.obrien.thecathedral.domain.usecase.GetPersonalizedScheduleUseCase
import com.obrien.thecathedral.domain.usecase.ToggleRitualUseCase
import com.obrien.thecathedral.model.Alarm
import com.obrien.thecathedral.model.Pillar
import com.obrien.thecathedral.model.PillarStatus
import com.obrien.thecathedral.util.computeStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

data class CodexUiState(
    val currentTime: LocalTime = LocalTime.now(),
    val pillars: List<Pillar> = emptyList(),
    val completedIds: Set<String> = emptySet(),
    val skippedIds: Set<String> = emptySet()
)

@HiltViewModel
class CodexViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    private val getPersonalizedSchedule: GetPersonalizedScheduleUseCase,
    private val toggleRitualUseCase: ToggleRitualUseCase
) : ViewModel() {

    private val _currentTime = MutableStateFlow(LocalTime.now())

    init {
        viewModelScope.launch {
            while (isActive) {
                _currentTime.value = LocalTime.now()
                delay(60_000L)
            }
        }
    }

    val uiState: StateFlow<CodexUiState> = combine(
        _currentTime,
        getPersonalizedSchedule(),
        repository.completedAlarms,
        repository.skippedAlarms
    ) { time, pillars, completed, skipped ->
        CodexUiState(time, pillars, completed, skipped)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CodexUiState()
    )

    fun toggleAlarm(alarmId: String) {
        viewModelScope.launch {
            toggleRitualUseCase(alarmId)
        }
    }

    fun toggleSkip(alarmId: String) {
        viewModelScope.launch {
            val skipped = repository.skippedAlarms.first()
            if (alarmId in skipped) {
                repository.markUnskipped(alarmId)
            } else {
                repository.markSkipped(alarmId)
            }
        }
    }

    fun getAlarmStatus(alarm: Alarm): PillarStatus {
        val completed = alarm.id in uiState.value.completedIds
        val skipped = alarm.id in uiState.value.skippedIds
        return alarm.computeStatus(completed, skipped, _currentTime.value)
    }
}
