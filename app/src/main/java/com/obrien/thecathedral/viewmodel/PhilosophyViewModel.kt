package com.obrien.thecathedral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.thecathedral.data.ScheduleRepository
import com.obrien.thecathedral.model.WeeklyReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhilosophyUiState(
    val activeSourceIndex: Int = 0,
    val activeSourcePage: Int = 0
)

@HiltViewModel
class PhilosophyViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    val uiState: StateFlow<PhilosophyUiState> = combine(
        repository.activeSourceIndex,
        repository.activeSourcePage
    ) { index, page ->
        PhilosophyUiState(index, page)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PhilosophyUiState()
    )

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

    fun saveWeeklyReview(review: WeeklyReview) {
        viewModelScope.launch {
            repository.saveWeeklyReview(review)
        }
    }
}
