package com.obrien.thecathedral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.thecathedral.data.ScheduleData
import com.obrien.thecathedral.data.ScheduleRepository
import com.obrien.thecathedral.model.Alarm
import com.obrien.thecathedral.model.JournalEntry
import com.obrien.thecathedral.model.Pillar
import com.obrien.thecathedral.model.PillarStatus
import com.obrien.thecathedral.util.computeStatus
import com.obrien.thecathedral.util.isActiveAt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class CathedralUiState(
    val currentTime: LocalTime = LocalTime.now(),
    val completedAlarmIds: Set<String> = emptySet(),
    val activePillar: Pillar? = null,
    val nextPillar: Pillar? = null,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val progress: Float = 0f,
    val journalEntries: List<JournalEntry> = emptyList(),
    val activeSourceIndex: Int = 0,
    val activeSourcePage: Int = 0,
    val pillars: List<Pillar> = emptyList(),
    val wakeTime: LocalTime = LocalTime.of(7, 0)
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _currentTime = MutableStateFlow(LocalTime.now())
    val currentTime: StateFlow<LocalTime> = _currentTime.asStateFlow()

    private val _completedAlarmIds = MutableStateFlow<Set<String>>(emptySet())
    private val _journalEntries = MutableStateFlow<List<JournalEntry>>(emptyList())
    private val _activeSourceIndex = MutableStateFlow(0)
    private val _activeSourcePage = MutableStateFlow(0)
    private val _wakeTime = MutableStateFlow(LocalTime.of(7, 0))

    init {
        viewModelScope.launch {
            repository.checkDailyReset()
            
            launch {
                repository.completedAlarms.collect { ids ->
                    _completedAlarmIds.value = ids
                }
            }
            launch {
                repository.journalEntries.collect { entries ->
                    _journalEntries.value = entries
                }
            }
            launch {
                repository.activeSourceIndex.collect { idx ->
                    _activeSourceIndex.value = idx
                }
            }
            launch {
                repository.activeSourcePage.collect { page ->
                    _activeSourcePage.value = page
                }
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
        combine(_currentTime, _completedAlarmIds, _journalEntries) { t, c, j -> Triple(t, c, j) },
        combine(_activeSourceIndex, _activeSourcePage, _wakeTime) { idx, page, wake -> Triple(idx, page, wake) }
    ) { part1, part2 ->
        val (time, completedIds, entries) = part1
        val (sourceIdx, sourcePage, wake) = part2
        
        val baseWakeTime = LocalTime.of(7, 0)
        val offset = Duration.between(baseWakeTime, wake)
        
        val shiftedPillars = ScheduleData.pillars.map { pillar ->
            val shiftedAlarms = pillar.alarms.map { alarm ->
                alarm.copy(time = alarm.time.plus(offset))
            }
            pillar.copy(alarms = shiftedAlarms)
        }

        val allAlarms = shiftedPillars.flatMap { it.alarms }
        val total = allAlarms.size
        val completedCount = allAlarms.count { it.id in completedIds }

        val activePillar = shiftedPillars.find { it.isActiveAt(time) }
        val nextPillar = shiftedPillars.find { pillar ->
            pillar.alarms.firstOrNull()?.time?.isAfter(time) == true
        }

        CathedralUiState(
            currentTime = time,
            completedAlarmIds = completedIds,
            activePillar = activePillar,
            nextPillar = nextPillar,
            completedCount = completedCount,
            totalCount = total,
            progress = if (total > 0) completedCount.toFloat() / total else 0f,
            journalEntries = entries,
            activeSourceIndex = sourceIdx,
            activeSourcePage = sourcePage,
            pillars = shiftedPillars,
            wakeTime = wake
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

    fun saveJournalEntry(entry: JournalEntry) {
        viewModelScope.launch {
            repository.saveJournalEntry(entry)
        }
    }

    fun setActiveSource(index: Int) {
        viewModelScope.launch {
            repository.setActiveSource(index)
        }
    }

    fun setActiveSourcePage(page: Int) {
        viewModelScope.launch {
            repository.setActiveSourcePage(page)
        }
    }

    fun setWakeTime(time: LocalTime) {
        _wakeTime.value = time
    }

    fun clearAllProgress() {
        viewModelScope.launch {
            repository.clearAllProgress()
        }
    }
}
