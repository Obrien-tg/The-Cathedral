package com.obrien.thelantern.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.core.data.ScheduleRepository
import com.obrien.core.model.WeeklyIntention
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeeklyIntentionUiState(
    val subjectFocus: String = "",
    val weekAim: String = "",
    val book: String = "",
    val bodyFocus: String = "",
    val characterAim: String = "",
    val weekStartDate: String = ""
) {
    fun isAnythingSet(): Boolean =
        subjectFocus.isNotBlank() ||
                weekAim.isNotBlank() ||
                book.isNotBlank() ||
                bodyFocus.isNotBlank() ||
                characterAim.isNotBlank()
}

@HiltViewModel
class WeeklyIntentionViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _subjectFocus = MutableStateFlow("")
    private val _weekAim = MutableStateFlow("")
    private val _book = MutableStateFlow("")
    private val _bodyFocus = MutableStateFlow("")
    private val _characterAim = MutableStateFlow("")

    init {
        viewModelScope.launch {
            val intention = repository.weeklyIntention.first()
            _subjectFocus.value = intention.subjectFocus
            _weekAim.value = intention.weeklyAim
            _book.value = intention.historiaBook
            _bodyFocus.value = intention.bodyFocus
            _characterAim.value = intention.characterAim
        }
    }

    val uiState: StateFlow<WeeklyIntentionUiState> = combine(
        _subjectFocus, _weekAim, _book,
        _bodyFocus, _characterAim
    ) { flows ->
        WeeklyIntentionUiState(
            subjectFocus = flows[0],
            weekAim = flows[1],
            book = flows[2],
            bodyFocus = flows[3],
            characterAim = flows[4],
            weekStartDate = repository.currentWeekStart()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WeeklyIntentionUiState(weekStartDate = repository.currentWeekStart())
    )

    fun updateSubjectFocus(value: String) { _subjectFocus.value = value }
    fun updateWeekAim(value: String) { _weekAim.value = value }
    fun updateBook(value: String) { _book.value = value }
    fun updateBodyFocus(value: String) { _bodyFocus.value = value }
    fun updateCharacterAim(value: String) { _characterAim.value = value }

    fun save() {
        viewModelScope.launch {
            val intention = WeeklyIntention(
                weekStartDate = repository.currentWeekStart(),
                subjectFocus = _subjectFocus.value,
                weeklyAim = _weekAim.value,
                historiaBook = _book.value,
                bodyFocus = _bodyFocus.value,
                characterAim = _characterAim.value
            )
            repository.saveWeeklyIntention(intention)
        }
    }

    fun clear() {
        _subjectFocus.value = ""
        _weekAim.value = ""
        _book.value = ""
        _bodyFocus.value = ""
        _characterAim.value = ""
        viewModelScope.launch {
            repository.clearWeeklyIntention()
        }
    }
}
