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
    val weekStartDate: String = ""
) {
    fun isAnythingSet(): Boolean =
        techneFocus.isNotBlank() ||
                historiaBook.isNotBlank() ||
                historiaResearch.isNotBlank() ||
                gymnosFocus.isNotBlank() ||
                sophiaTheme.isNotBlank() ||
                weeklyAim.isNotBlank()
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

    init {
        viewModelScope.launch {
            val intention = repository.weeklyIntention.first()
            _techneFocus.value = intention.techneFocus
            _historiaBook.value = intention.historiaBook
            _historiaResearch.value = intention.historiaResearch
            _gymnosFocus.value = intention.gymnosFocus
            _sophiaTheme.value = intention.sophiaTheme
            _weeklyAim.value = intention.weeklyAim
        }
    }

    val uiState: StateFlow<WeeklyIntentionUiState> = combine(
        _techneFocus, _historiaBook, _historiaResearch,
        _gymnosFocus, _sophiaTheme, _weeklyAim
    ) { flows ->
        WeeklyIntentionUiState(
            techneFocus = flows[0],
            historiaBook = flows[1],
            historiaResearch = flows[2],
            gymnosFocus = flows[3],
            sophiaTheme = flows[4],
            weeklyAim = flows[5],
            weekStartDate = repository.currentWeekStart()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WeeklyIntentionUiState(weekStartDate = repository.currentWeekStart())
    )

    fun updateTechneFocus(value: String) { _techneFocus.value = value }
    fun updateHistoriaBook(value: String) { _historiaBook.value = value }
    fun updateHistoriaResearch(value: String) { _historiaResearch.value = value }
    fun updateGymnosFocus(value: String) { _gymnosFocus.value = value }
    fun updateSophiaTheme(value: String) { _sophiaTheme.value = value }
    fun updateWeeklyAim(value: String) { _weeklyAim.value = value }

    fun save() {
        viewModelScope.launch {
            val intention = WeeklyIntention(
                weekStartDate = repository.currentWeekStart(),
                techneFocus = _techneFocus.value,
                historiaBook = _historiaBook.value,
                historiaResearch = _historiaResearch.value,
                gymnosFocus = _gymnosFocus.value,
                sophiaTheme = _sophiaTheme.value,
                weeklyAim = _weeklyAim.value
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
        viewModelScope.launch {
            repository.clearWeeklyIntention()
        }
    }
}
