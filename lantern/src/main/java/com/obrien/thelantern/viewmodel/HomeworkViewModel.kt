package com.obrien.thelantern.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.core.data.HomeworkDao
import com.obrien.core.model.HomeworkEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

data class HomeworkUiState(
    val entries: List<HomeworkEntry> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeworkViewModel @Inject constructor(
    private val homeworkDao: HomeworkDao
) : ViewModel() {

    private val today = LocalDate.now().toString()

    val uiState: StateFlow<HomeworkUiState> = homeworkDao.getHomeworkForDate(today)
        .map { HomeworkUiState(entries = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeworkUiState()
        )

    fun addSubject(subject: String) {
        viewModelScope.launch {
            val entry = HomeworkEntry(
                id = UUID.randomUUID().toString(),
                date = today,
                subject = subject,
                isCompleted = false
            )
            homeworkDao.insert(entry)
        }
    }

    fun removeSubject(subject: String) {
        viewModelScope.launch {
            val entry = uiState.value.entries.find { it.subject == subject }
            if (entry != null) {
                homeworkDao.delete(entry)
            }
        }
    }

    fun updateDetail(id: String, description: String, whatILearned: String) {
        viewModelScope.launch {
            val entry = uiState.value.entries.find { it.id == id }
            if (entry != null) {
                homeworkDao.insert(entry.copy(
                    description = description,
                    whatILearned = whatILearned
                ))
            }
        }
    }

    fun toggleCompletion(id: String, completed: Boolean) {
        viewModelScope.launch {
            homeworkDao.updateCompletion(id, completed)
        }
    }
}
