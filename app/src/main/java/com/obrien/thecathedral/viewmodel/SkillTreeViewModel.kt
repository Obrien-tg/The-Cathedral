package com.obrien.thecathedral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.thecathedral.domain.usecase.GetSkillProgressUseCase
import com.obrien.thecathedral.model.SkillProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SkillTreeUiState(
    val skillProgress: List<SkillProgress> = emptyList()
)

@HiltViewModel
class SkillTreeViewModel @Inject constructor(
    private val getSkillProgress: GetSkillProgressUseCase
) : ViewModel() {

    val uiState: StateFlow<SkillTreeUiState> = getSkillProgress()
        .map { SkillTreeUiState(skillProgress = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SkillTreeUiState()
        )
}
