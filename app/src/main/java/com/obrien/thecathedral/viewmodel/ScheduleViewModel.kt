package com.obrien.thecathedral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.thecathedral.data.ScheduleData
import com.obrien.thecathedral.data.ScheduleRepository
import com.obrien.thecathedral.model.Alarm
import com.obrien.thecathedral.model.DailyCounsel
import com.obrien.thecathedral.model.DailyCounselData
import com.obrien.thecathedral.model.JournalEntry
import com.obrien.thecathedral.model.WeeklyReview
import com.obrien.thecathedral.model.Pillar
import com.obrien.thecathedral.model.PillarStatus
import com.obrien.thecathedral.model.SkillProgress
import com.obrien.thecathedral.model.SkillTreeData
import com.obrien.thecathedral.service.ForgeService
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
import java.time.LocalTime
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
    val wakeTime: LocalTime = LocalTime.of(7, 0),
    val skillProgress: List<SkillProgress> = emptyList(),
    val historicalCompletions: Map<String, Int> = emptyMap(),
    val totalFocusSessions: Int = 0,
    val completionHistory: Map<String, Int> = emptyMap(),
    val todayCounsel: DailyCounsel = DailyCounselData.today(),
    val weeklyReviews: List<WeeklyReview> = emptyList()
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    private val application: android.app.Application
) : ViewModel() {

    private val _currentTime = MutableStateFlow(LocalTime.now())
    val currentTime: StateFlow<LocalTime> = _currentTime.asStateFlow()

    private val _completedAlarmIds = MutableStateFlow<Set<String>>(emptySet())
    private val _journalEntries = MutableStateFlow<List<JournalEntry>>(emptyList())
    private val _activeSourceIndex = MutableStateFlow(0)
    private val _activeSourcePage = MutableStateFlow(0)
    private val _wakeTime = MutableStateFlow(LocalTime.of(7, 0))
    private val _historicalCompletions = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val _totalFocusSessions = MutableStateFlow(0)
    private val _completionHistory = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val _weeklyReviews = MutableStateFlow<List<WeeklyReview>>(emptyList())

    // Focus Timer State
    private val _focusTimeRemaining = MutableStateFlow(25 * 60)
    val focusTimeRemaining: StateFlow<Int> = _focusTimeRemaining.asStateFlow()

    private val _focusIsRunning = MutableStateFlow(false)
    val focusIsRunning: StateFlow<Boolean> = _focusIsRunning.asStateFlow()

    private val _focusSessionCount = MutableStateFlow(0)
    val focusSessionCount: StateFlow<Int> = _focusSessionCount.asStateFlow()

    private var forgeService: ForgeService? = null
    private val serviceConnection = object : android.content.ServiceConnection {
        override fun onServiceConnected(name: android.content.ComponentName?, service: android.os.IBinder?) {
            val binder = service as ForgeService.ForgeBinder
            forgeService = binder.getService()
            
            // Link service flows to ViewModel state
            viewModelScope.launch {
                forgeService?.timeRemaining?.collect { _focusTimeRemaining.value = it }
            }
            viewModelScope.launch {
                forgeService?.isRunning?.collect { _focusIsRunning.value = it }
            }
        }

        override fun onServiceDisconnected(name: android.content.ComponentName?) {
            forgeService = null
        }
    }

    init {
        val intent = android.content.Intent(application, ForgeService::class.java)
        application.bindService(intent, serviceConnection, android.content.Context.BIND_AUTO_CREATE)
        
        viewModelScope.launch {
            // Daily reset
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
            launch {
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
            launch {
                repository.historicalCompletions.collect { 
                    _historicalCompletions.value = it 
                }
            }
            launch {
                repository.totalFocusSessions.collect { 
                    _totalFocusSessions.value = it 
                    _focusSessionCount.value = it 
                }
            }
            launch {
                repository.completionHistory.collect {
                    _completionHistory.value = it
                }
            }
            launch {
                repository.weeklyReviews.collect {
                    _weeklyReviews.value = it
                }
            }
        }

        // Clock ticker (every minute)
        viewModelScope.launch {
            while (isActive) {
                _currentTime.value = LocalTime.now()
                delay(60_000L)
            }
        }
    }

    val uiState: StateFlow<CathedralUiState> = combine(
        combine(_currentTime, _completedAlarmIds, _journalEntries) { t, c, j -> Triple(t, c, j) },
        combine(_activeSourceIndex, _activeSourcePage, _wakeTime) { idx, page, wake -> Triple(idx, page, wake) },
        combine(_historicalCompletions, _totalFocusSessions, _completionHistory) { h, f, ch -> Triple(h, f, ch) },
        _weeklyReviews
    ) { part1, part2, part3, weeklyReviews ->
        val (time, completedIds, entries) = part1
        val (sourceIdx, sourcePage, wake) = part2
        val (historical, focusSessions, historyMap) = part3
        
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

        val skillProgress = computeSkillProgress(historical, focusSessions, entries)

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
            wakeTime = wake,
            skillProgress = skillProgress,
            historicalCompletions = historical,
            totalFocusSessions = focusSessions,
            completionHistory = historyMap,
            todayCounsel = DailyCounselData.today(),
            weeklyReviews = weeklyReviews
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CathedralUiState()
    )

    private fun computeSkillProgress(
        historical: Map<String, Int>,
        focusSessions: Int,
        journalEntries: List<JournalEntry>
    ): List<SkillProgress> {
        val journalDays = journalEntries.map { it.date }.toSet().size

        // First pass – calculate raw progress for each node based on lifetime record
        val raw = SkillTreeData.nodes.associate { node ->
            val alarmHits = when (node.pillar) {
                "AWAKENING" -> historical["ignition"] ?: 0
                "TECHNE" -> listOf("deep_work_1", "deep_work_2", "commit")
                    .sumOf { historical[it] ?: 0 }
                "HISTORIA" -> listOf("primary_source", "peripatetic")
                    .sumOf { historical[it] ?: 0 }
                "GYMNOS" -> historical["physical"] ?: 0
                "SOPHIA" -> historical["digital_sunset"] ?: 0
                else -> 0
            }

            val completionRatio = (alarmHits.toFloat() / node.requiredCompletions.coerceAtLeast(1))
                .coerceAtMost(1f)
            val focusRatio = if (node.requiredFocusSessions > 0) {
                (focusSessions.toFloat() / node.requiredFocusSessions).coerceAtMost(1f)
            } else 1f
            val journalRatio = if (node.requiredJournalDays > 0) {
                (journalDays.toFloat() / node.requiredJournalDays).coerceAtMost(1f)
            } else 1f

            val overall = minOf(completionRatio, focusRatio, journalRatio)
            node.id to overall
        }

        // Second pass – determine unlock status (all parents must be completed)
        return SkillTreeData.nodes.map { node ->
            val progress = raw[node.id] ?: 0f
            val completed = progress >= 1f

            val parents = SkillTreeData.edges.filter { it.to == node.id }.map { it.from }
            val unlocked = parents.isEmpty() || parents.all { parentId ->
                (raw[parentId] ?: 0f) >= 1f
            }

            SkillProgress(
                nodeId = node.id,
                unlocked = unlocked,
                completed = completed,
                progress = progress
            )
        }
    }

    fun toggleAlarm(alarmId: String) {
        viewModelScope.launch {
            if (alarmId in _completedAlarmIds.value) {
                repository.markIncomplete(alarmId)
            } else {
                repository.markComplete(alarmId)
                repository.incrementHistoricalCompletion(alarmId)
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
        viewModelScope.launch {
            repository.setWakeTime(time.toString())
        }
    }

    fun clearAllProgress() {
        viewModelScope.launch {
            repository.clearAllProgress()
        }
    }

    fun saveWeeklyReview(review: WeeklyReview) {
        viewModelScope.launch {
            repository.saveWeeklyReview(review)
        }
    }

    // Focus Timer Actions
    fun startFocusTimer() {
        val intent = android.content.Intent(application, ForgeService::class.java).apply {
            action = ForgeService.ACTION_START
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            application.startForegroundService(intent)
        } else {
            application.startService(intent)
        }
    }

    fun pauseFocusTimer() {
        val intent = android.content.Intent(application, ForgeService::class.java).apply {
            action = ForgeService.ACTION_PAUSE
        }
        application.startService(intent)
    }

    fun resetFocusTimer() {
        val intent = android.content.Intent(application, ForgeService::class.java).apply {
            action = ForgeService.ACTION_RESET
        }
        application.startService(intent)
    }

    fun setFocusBreak() {
        val intent = android.content.Intent(application, ForgeService::class.java).apply {
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
