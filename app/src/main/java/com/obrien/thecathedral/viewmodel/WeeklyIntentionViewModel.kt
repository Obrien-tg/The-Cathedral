package com.obrien.thecathedral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.thecathedral.data.ScheduleRepository
import com.obrien.thecathedral.model.WeeklyIntention
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeeklyIntentionUiState(
    val techneProject: String = "",
    val techneSkill: String = "",
    val historiaBook: String = "",
    val historiaTopic: String = "",
    val gymnosFocus: String = "",
    val sophiaTheme: String = "",
    val weekNote: String = "",
    val weekStart: String = ""
)

@HiltViewModel
class WeeklyIntentionViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _techneProject = MutableStateFlow("")
    private val _techneSkill = MutableStateFlow("")
    private val _historiaBook = MutableStateFlow("")
    private val _historiaTopic = MutableStateFlow("")
    private val _gymnosFocus = MutableStateFlow("")
    private val _sophiaTheme = MutableStateFlow("")
    private val _weekNote = MutableStateFlow("")

    init {
        viewModelScope.launch {
            val intention = repository.weeklyIntention.first()
            intention?.let {
                _techneProject.value = it.techneProject
                _techneSkill.value = it.techneSkill
                _historiaBook.value = it.historiaBook
                _historiaTopic.value = it.historiaTopic
                _gymnosFocus.value = it.gymnosFocus
                _sophiaTheme.value = it.sophiaTheme
                _weekNote.value = it.weekNote
            }
        }
    }

    val uiState: StateFlow<WeeklyIntentionUiState> = combine(
        _techneProject, _techneSkill, _historiaBook, _historiaTopic,
        _gymnosFocus, _sophiaTheme, _weekNote
    ) { flows ->
        WeeklyIntentionUiState(
            techneProject = flows[0],
            techneSkill = flows[1],
            historiaBook = flows[2],
            historiaTopic = flows[3],
            gymnosFocus = flows[4],
            sophiaTheme = flows[5],
            weekNote = flows[6],
            weekStart = repository.currentWeekStart()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WeeklyIntentionUiState(weekStart = repository.currentWeekStart())
    )

    fun updateTechneProject(value: String) { _techneProject.value = value }
    fun updateTechneSkill(value: String) { _techneSkill.value = value }
    fun updateHistoriaBook(value: String) { _historiaBook.value = value }
    fun updateHistoriaTopic(value: String) { _historiaTopic.value = value }
    fun updateGymnosFocus(value: String) { _gymnosFocus.value = value }
    fun updateSophiaTheme(value: String) { _sophiaTheme.value = value }
    fun updateWeekNote(value: String) { _weekNote.value = value }

    fun save() {
        viewModelScope.launch {
            val intention = WeeklyIntention(
                weekStart = repository.currentWeekStart(),
                techneProject = _techneProject.value,
                techneSkill = _techneSkill.value,
                historiaBook = _historiaBook.value,
                historiaTopic = _historiaTopic.value,
                gymnosFocus = _gymnosFocus.value,
                sophiaTheme = _sophiaTheme.value,
                weekNote = _weekNote.value
            )
            repository.saveWeeklyIntention(intention)
        }
    }

    fun clear() {
        _techneProject.value = ""
        _techneSkill.value = ""
        _historiaBook.value = ""
        _historiaTopic.value = ""
        _gymnosFocus.value = ""
        _sophiaTheme.value = ""
        _weekNote.value = ""
        viewModelScope.launch {
            repository.clearWeeklyIntention()
        }
    }
}
