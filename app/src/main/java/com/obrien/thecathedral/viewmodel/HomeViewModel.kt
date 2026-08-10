package com.obrien.thecathedral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.thecathedral.data.ScheduleRepository
import com.obrien.thecathedral.domain.usecase.*
import com.obrien.thecathedral.model.DailyCounsel
import com.obrien.thecathedral.model.DailyCounselData
import com.obrien.thecathedral.model.Pillar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

data class HomeUiState(
    val currentTime: LocalTime = LocalTime.now(),
    val activePillar: Pillar? = null,
    val nextPillar: Pillar? = null,
    val score: DailyScore = DailyScore(0, 0, 0f),
    val completionHistory: Map<String, Int> = emptyMap(),
    val todayCounsel: DailyCounsel = DailyCounselData.today(),
    val showAccountabilityDialog: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    private val getActivePillar: GetActivePillarUseCase,
    private val getNextPillar: GetNextPillarUseCase,
    private val getDailyScore: GetDailyScoreUseCase
) : ViewModel() {

    private val _currentTime = MutableStateFlow(LocalTime.now())
    private val _lastAccountabilityAcknowledgeDate = MutableStateFlow("")

    init {
        viewModelScope.launch {
            repository.checkDailyReset()
        }

        viewModelScope.launch {
            repository.lastAccountabilityAcknowledgeDate.collect {
                _lastAccountabilityAcknowledgeDate.value = it
            }
        }

        viewModelScope.launch {
            while (isActive) {
                _currentTime.value = LocalTime.now()
                delay(60_000L)
            }
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        combine(_currentTime, getActivePillar(_currentTime), getNextPillar(_currentTime)) { t, a, n -> Triple(t, a, n) },
        combine(getDailyScore(), repository.completionHistory, _lastAccountabilityAcknowledgeDate) { s, h, l -> Triple(s, h, l) }
    ) { part1, part2 ->
        val (time, active, next) = part1
        val (score, history, lastAcknowledge) = part2
        
        // 2-Day Rule logic
        val todayStr = java.time.LocalDate.now().toString()
        val yesterday = java.time.LocalDate.now().minusDays(1).toString()
        val dayBefore = java.time.LocalDate.now().minusDays(2).toString()
        val missedYesterday = (history[yesterday] ?: 0) == 0
        val missedDayBefore = (history[dayBefore] ?: 0) == 0
        val showAccountability = missedYesterday && missedDayBefore && lastAcknowledge != todayStr

        HomeUiState(
            currentTime = time,
            activePillar = active,
            nextPillar = next,
            score = score,
            completionHistory = history,
            todayCounsel = DailyCounselData.today(),
            showAccountabilityDialog = showAccountability
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun clearAllProgress() {
        viewModelScope.launch {
            repository.clearAllProgress()
        }
    }

    fun dismissAccountabilityDialog() {
        viewModelScope.launch {
            repository.acknowledgeAccountability()
        }
    }
}
