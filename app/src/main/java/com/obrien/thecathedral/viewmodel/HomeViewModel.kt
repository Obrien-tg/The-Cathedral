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
import java.time.LocalDate
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
    val currentStreak: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    private val getActivePillar: GetActivePillarUseCase,
    private val getNextPillar: GetNextPillarUseCase,
    private val getDailyScore: GetDailyScoreUseCase
) : ViewModel() {

    private val _currentTime = MutableStateFlow(LocalTime.now())
    private val _manualDismiss = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.checkDailyReset()
        }

        viewModelScope.launch {
            while (isActive) {
                _currentTime.value = LocalTime.now()
                delay(60_000L)
            }
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        _currentTime,
        getActivePillar(_currentTime),
        getNextPillar(_currentTime),
        getDailyScore(),
        repository.completionHistory,
        repository.lastAccountabilityAcknowledgeDate,
        _manualDismiss
    ) { flows ->
        val time = flows[0] as LocalTime
        val active = flows[1] as? Pillar
        val next = flows[2] as? Pillar
        val score = flows[3] as DailyScore
        val history = flows[4] as Map<String, Int>
        val lastAcknowledge = flows[5] as String
        val manualDismiss = flows[6] as Boolean
        
        val todayStr = LocalDate.now().toString()
        val yesterday = LocalDate.now().minusDays(1).toString()
        val dayBefore = LocalDate.now().minusDays(2).toString()
        
        val missedYesterday = (history[yesterday] ?: 0) == 0
        val missedDayBefore = (history[dayBefore] ?: 0) == 0
        
        // Show dialog if missed 2 days, haven't acknowledged today, and haven't manually dismissed this session
        val shouldShow = missedYesterday && missedDayBefore && 
                        lastAcknowledge != todayStr && !manualDismiss

        HomeUiState(
            currentTime = time,
            activePillar = active,
            nextPillar = next,
            score = score,
            completionHistory = history,
            todayCounsel = DailyCounselData.today(),
            showAccountabilityDialog = shouldShow,
            currentStreak = calculateStreak(history, score.totalCount)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    private fun calculateStreak(history: Map<String, Int>, totalCount: Int): Int {
        if (totalCount <= 0) return 0
        var streak = 0
        var checkDate = LocalDate.now()
        
        // If today's rituals aren't finished, start checking from yesterday for the streak
        if ((history[checkDate.toString()] ?: 0) < totalCount) {
            checkDate = checkDate.minusDays(1)
        }
        
        while ((history[checkDate.toString()] ?: 0) >= totalCount) {
            streak++
            checkDate = checkDate.minusDays(1)
            if (streak > 3650) break // 10 year safety limit
        }
        return streak
    }

    fun clearAllProgress() {
        viewModelScope.launch {
            repository.clearAllProgress()
        }
    }

    fun dismissAccountabilityDialog() {
        _manualDismiss.value = true // Immediate UI feedback
        viewModelScope.launch {
            repository.acknowledgeAccountability()
        }
    }
}
