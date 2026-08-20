package com.obrien.thelantern.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.obrien.core.focus.BaseFocusViewModel
import com.obrien.core.data.ScheduleRepository
import com.obrien.core.focus.FocusKind
import com.obrien.core.model.Pillar
import com.obrien.core.model.WeeklyIntention
import com.obrien.thelantern.domain.usecase.GetActivePillarUseCase
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
            intention.weeklyAim.isNotBlank() -> "Goal: ${intention.weeklyAim}"
            intention.subjectFocus.isNotBlank() -> "Study: ${intention.subjectFocus}"
            pillar?.id == "study" -> "Forge your mind — focus deep"
            pillar?.id == "school" -> "Presence and learning"
            else -> "What is the one thing to finish now?"
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "What are you working on?"
    )

    fun startDeepWork(target: String, durationMins: Int) {
        startFocus(FocusKind.DEEP_WORK, target, durationMins, "com.obrien.thelantern.MainActivity")
    }

    fun startMindfulness(durationMins: Int) {
        startFocus(FocusKind.MINDFULNESS, "Quiet Light", durationMins, "com.obrien.thelantern.MainActivity")
    }
}
