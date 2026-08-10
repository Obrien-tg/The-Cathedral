package com.obrien.thecathedral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.thecathedral.data.ScheduleRepository
import com.obrien.thecathedral.model.JournalEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JournalUiState(
    val journalEntries: List<JournalEntry> = emptyList()
)

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    val uiState: StateFlow<JournalUiState> = repository.journalEntries
        .map { JournalUiState(journalEntries = it) }
        .stateIn(
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
