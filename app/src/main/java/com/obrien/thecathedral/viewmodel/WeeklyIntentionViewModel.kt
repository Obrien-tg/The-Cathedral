package com.obrien.thecathedral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.core.data.ScheduleRepository
import com.obrien.core.model.WeeklyIntention
import com.obrien.thecathedral.data.DayScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

data class WeeklyIntentionUiState(
    val techneFocus: String = "",
    val historiaBook: String = "",
    val historiaResearch: String = "",
    val gymnosFocus: String = "",
    val sophiaTheme: String = "",
    val weeklyAim: String = "",
    val explorations: String = "",
    val targetWakeTime: String = "",
    val targetSleepTime: String = "",
    val dailyFocusHours: Float = 0f,
    // The week this planning session is writing to (upcoming Monday when set on Sunday)
    val weekStartDate: String = WeeklyIntention.weekStartForSave()
) {
    val isUpcomingWeek: Boolean
        get() = weekStartDate == WeeklyIntention.nextWeekStart()

    fun isAnythingSet(): Boolean =
        techneFocus.isNotBlank() ||
                historiaBook.isNotBlank() ||
                historiaResearch.isNotBlank() ||
                gymnosFocus.isNotBlank() ||
                sophiaTheme.isNotBlank() ||
                weeklyAim.isNotBlank() ||
                explorations.isNotBlank() ||
                targetWakeTime.isNotBlank() ||
                targetSleepTime.isNotBlank() ||
                dailyFocusHours > 0f

    /** Monastic-voiced reason the declared hours cannot hold, or null if sound. */
    val structuralError: String?
        get() = DayScheduler.validate(targetWakeTime, targetSleepTime, dailyFocusHours)

    val focusLabel: String
        get() = when {
            dailyFocusHours <= 0f -> "—"
            dailyFocusHours == dailyFocusHours.toInt().toFloat() ->
                dailyFocusHours.toInt().toString()
            else -> String.format("%.1f", dailyFocusHours)
        }
}

@HiltViewModel
class WeeklyIntentionViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _techneFocus = MutableStateFlow("")
    private val _historiaBook = MutableStateFlow("")
    private val _historiaResearch = MutableStateFlow("")
    private val _gymnosFocus = MutableStateFlow("")
    private val _sophiaTheme = MutableStateFlow("")
    private val _weeklyAim = MutableStateFlow("")
    private val _explorations = MutableStateFlow("")
    private val _targetWakeTime = MutableStateFlow("")
    private val _targetSleepTime = MutableStateFlow("")
    private val _dailyFocusHours = MutableStateFlow(0f)

    init {
        viewModelScope.launch {
            val saved = repository.weeklyIntentionRaw.first()
            // Only surface a saved quest set if it belongs to the week in
            // progress or the week being planned — never last week's text.
            val relevant = saved.weekStartDate == WeeklyIntention.currentWeekStart() ||
                    saved.weekStartDate == WeeklyIntention.nextWeekStart()
            if (relevant) {
                _techneFocus.value = saved.techneFocus
                _historiaBook.value = saved.historiaBook
                _historiaResearch.value = saved.historiaResearch
                _gymnosFocus.value = saved.gymnosFocus
                _sophiaTheme.value = saved.sophiaTheme
                _weeklyAim.value = saved.weeklyAim
                _explorations.value = saved.explorations
                _targetWakeTime.value = saved.targetWakeTime
                _targetSleepTime.value = saved.targetSleepTime
                _dailyFocusHours.value = saved.dailyFocusHours
            }
        }
    }

    val uiState: StateFlow<WeeklyIntentionUiState> = combine(
        listOf<Flow<Any>>(
            _techneFocus, _historiaBook, _historiaResearch,
            _gymnosFocus, _sophiaTheme, _weeklyAim, _explorations,
            _targetWakeTime, _targetSleepTime, _dailyFocusHours
        )
    ) { a ->
        WeeklyIntentionUiState(
            techneFocus = a[0] as String,
            historiaBook = a[1] as String,
            historiaResearch = a[2] as String,
            gymnosFocus = a[3] as String,
            sophiaTheme = a[4] as String,
            weeklyAim = a[5] as String,
            explorations = a[6] as String,
            targetWakeTime = a[7] as String,
            targetSleepTime = a[8] as String,
            dailyFocusHours = a[9] as Float,
            weekStartDate = WeeklyIntention.weekStartForSave()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WeeklyIntentionUiState(weekStartDate = WeeklyIntention.weekStartForSave())
    )

    fun updateTechneFocus(value: String) { _techneFocus.value = value }
    fun updateHistoriaBook(value: String) { _historiaBook.value = value }
    fun updateHistoriaResearch(value: String) { _historiaResearch.value = value }
    fun updateGymnosFocus(value: String) { _gymnosFocus.value = value }
    fun updateSophiaTheme(value: String) { _sophiaTheme.value = value }
    fun updateWeeklyAim(value: String) { _weeklyAim.value = value }
    fun updateExplorations(value: String) { _explorations.value = value }
    fun updateTargetWakeTime(value: String) { _targetWakeTime.value = value }
    fun updateTargetSleepTime(value: String) { _targetSleepTime.value = value }

    /** Half-hour steps, clamped to 1–6h — honest capacity, never heroics. */
    fun adjustFocusHours(delta: Float) {
        val snapped = (((_dailyFocusHours.value + delta) * 2).roundToInt() / 2f)
        _dailyFocusHours.value = snapped.coerceIn(1f, 6f)
    }

    fun save() {
        if (uiState.value.structuralError != null) return
        viewModelScope.launch {
            val intention = WeeklyIntention(
                weekStartDate = WeeklyIntention.weekStartForSave(),
                techneFocus = _techneFocus.value,
                historiaBook = _historiaBook.value,
                historiaResearch = _historiaResearch.value,
                gymnosFocus = _gymnosFocus.value,
                sophiaTheme = _sophiaTheme.value,
                weeklyAim = _weeklyAim.value,
                explorations = _explorations.value,
                targetWakeTime = _targetWakeTime.value,
                targetSleepTime = _targetSleepTime.value,
                dailyFocusHours = _dailyFocusHours.value
            )
            repository.saveWeeklyIntention(intention)
            if (_targetWakeTime.value.isNotBlank()) {
                repository.setWakeTime(_targetWakeTime.value)
            }
        }
    }

    fun clear() {
        _techneFocus.value = ""
        _historiaBook.value = ""
        _historiaResearch.value = ""
        _gymnosFocus.value = ""
        _sophiaTheme.value = ""
        _weeklyAim.value = ""
        _explorations.value = ""
        _targetWakeTime.value = ""
        _targetSleepTime.value = ""
        _dailyFocusHours.value = 0f
        viewModelScope.launch {
            repository.clearWeeklyIntention()
        }
    }
}
