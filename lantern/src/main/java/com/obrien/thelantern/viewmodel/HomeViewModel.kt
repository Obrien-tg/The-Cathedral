package com.obrien.thelantern.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.core.data.ScheduleRepository
import com.obrien.thelantern.domain.usecase.*
import com.obrien.thelantern.model.DailyCounsel
import com.obrien.thelantern.model.DailyCounselData
import com.obrien.core.model.Pillar
import com.obrien.core.model.WeeklyIntention
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
    val showAccountabilityDialog: Boolean = false,
    val weeklyIntention: WeeklyIntention = WeeklyIntention(),
    val hasWeekRule: Boolean = false
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
        combine(_currentTime, getActivePillar(_currentTime), getNextPillar(_currentTime)) { t: LocalTime, a: Pillar?, n: Pillar? -> Triple(t, a, n) },
        combine(getDailyScore(), repository.completionHistory, _lastAccountabilityAcknowledgeDate) { s: DailyScore, h: Map<String, Int>, l: String -> Triple(s, h, l) },
        repository.weeklyIntention
    ) { part1: Triple<LocalTime, Pillar?, Pillar?>, part2: Triple<DailyScore, Map<String, Int>, String>, intention: WeeklyIntention ->
        val (time, active, next) = part1
        val (score, history, lastAcknowledge) = part2

        // 2-Day Rule logic
        val todayStr = java.time.LocalDate.now().toString()
        val yesterday = java.time.LocalDate.now().minusDays(1).toString()
        val dayBefore = java.time.LocalDate.now().minusDays(2).toString()
        val missedYesterday = (history[yesterday] ?: 0) == 0
        val missedDayBefore = (history[dayBefore] ?: 0) == 0
        val hasAnyHistory = history.isNotEmpty()
        val showAccountability = hasAnyHistory && missedYesterday && missedDayBefore && lastAcknowledge != todayStr

        val activeIntention = if (intention.isActiveForCurrentWeek()) intention else WeeklyIntention.emptyForCurrentWeek()

        HomeUiState(
            currentTime = time,
            activePillar = active,
            nextPillar = next,
            score = score,
            completionHistory = history,
            todayCounsel = DailyCounselData.today(),
            showAccountabilityDialog = showAccountability,
            weeklyIntention = activeIntention,
            hasWeekRule = activeIntention.hasAnyFocus
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun acknowledgeAccountability() {
        viewModelScope.launch {
            repository.acknowledgeAccountability()
        }
    }

    fun resetDay() {
        viewModelScope.launch {
            repository.clearAllProgress()
        }
    }

    fun clearAllProgress() {
        viewModelScope.launch {
            repository.clearAllProgress()
        }
    }
}
