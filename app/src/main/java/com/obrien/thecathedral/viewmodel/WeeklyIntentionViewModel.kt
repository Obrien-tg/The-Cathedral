package com.obrien.thecathedral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.core.data.ScheduleRepository
import com.obrien.core.model.WeeklyIntention
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeeklyIntentionUiState(
    val techneFocus: String = "",
    val historiaBook: String = "",
    val historiaResearch: String = "",
    val gymnosFocus: String = "",
    val sophiaTheme: String = "",
    val weeklyAim: String = "",
    val explorations: String = "",
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
                explorations.isNotBlank()
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
            }
        }
    }

    val uiState: StateFlow<WeeklyIntentionUiState> = combine(
        _techneFocus, _historiaBook, _historiaResearch,
        _gymnosFocus, _sophiaTheme, _weeklyAim, _explorations
    ) { flows ->
        WeeklyIntentionUiState(
            techneFocus = flows[0],
            historiaBook = flows[1],
            historiaResearch = flows[2],
            gymnosFocus = flows[3],
            sophiaTheme = flows[4],
            weeklyAim = flows[5],
            explorations = flows[6],
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

    fun save() {
        viewModelScope.launch {
            val intention = WeeklyIntention(
                weekStartDate = WeeklyIntention.weekStartForSave(),
                techneFocus = _techneFocus.value,
                historiaBook = _historiaBook.value,
                historiaResearch = _historiaResearch.value,
                gymnosFocus = _gymnosFocus.value,
                sophiaTheme = _sophiaTheme.value,
                weeklyAim = _weeklyAim.value,
                explorations = _explorations.value
            )
            repository.saveWeeklyIntention(intention)
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
        viewModelScope.launch {
            repository.clearWeeklyIntention()
        }
    }
}
