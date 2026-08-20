package com.obrien.thecathedral.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.obrien.core.focus.BaseFocusViewModel
import com.obrien.core.data.ScheduleRepository
import com.obrien.core.focus.FocusKind
import com.obrien.core.model.Pillar
import com.obrien.core.model.WeeklyIntention
import com.obrien.thecathedral.domain.usecase.GetActivePillarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    application: Application,
    repository: ScheduleRepository,
    private val getActivePillar: GetActivePillarUseCase
) : BaseFocusViewModel(application, repository) {

    private val _currentTime = MutableStateFlow(java.time.LocalTime.now())

    val suggestedPrompt: StateFlow<String> = combine(
        getActivePillar(_currentTime),
        repository.weeklyIntention
    ) { pillar: Pillar?, intention: WeeklyIntention ->
        when {
            intention.techneFocus.isNotBlank() -> "Build: ${intention.techneFocus}"
            pillar?.id == "forge" -> "Deep work — one concrete step"
            pillar?.id == "archive" -> "Primary source — one claim + one question"
            else -> "What is the one outcome of this block?"
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "What are you focusing on?"
    )

    fun startDeepWork(target: String, durationMins: Int) {
        startFocus(FocusKind.DEEP_WORK, target, durationMins, "com.obrien.thecathedral.MainActivity")
    }

    fun startMindfulness(durationMins: Int) {
        startFocus(FocusKind.MINDFULNESS, "Sophia Sanctuary", durationMins, "com.obrien.thecathedral.MainActivity")
    }
}
