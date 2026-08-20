package com.obrien.thelantern.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.core.data.ScheduleRepository
import com.obrien.core.model.JournalEntry
import com.obrien.core.model.WeeklyIntention
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JournalUiState(
    val journalEntries: List<JournalEntry> = emptyList(),
    val weeklyIntention: WeeklyIntention? = null
)

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    val uiState: StateFlow<JournalUiState> = combine(
        repository.journalEntries,
        repository.weeklyIntention
    ) { entries: List<JournalEntry>, intention: WeeklyIntention ->
        JournalUiState(journalEntries = entries, weeklyIntention = intention)
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = JournalUiState()
        )

    fun saveJournalEntry(entry: JournalEntry) {
        viewModelScope.launch {
            repository.saveJournalEntry(entry)
        }
    }
}
