package com.obrien.thecathedral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrien.core.data.ScheduleRepository
import com.obrien.thecathedral.domain.usecase.GetSkillProgressUseCase
import com.obrien.core.model.JournalEntry
import com.obrien.core.model.SkillNode
import com.obrien.core.model.SkillProgress
import com.obrien.thecathedral.model.SkillTreeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SkillTreeUiState(
    val skillProgress: List<SkillProgress> = emptyList(),
    val historicalCompletions: Map<String, Int> = emptyMap(),
    val totalFocusSessions: Int = 0,
    val journalDays: Int = 0,
    val selectedNodeId: String? = null
) {
    val selectedNode: SkillNode?
        get() = selectedNodeId?.let { SkillTreeData.nodeById(it) }

    val selectedProgress: SkillProgress?
        get() = selectedNodeId?.let { id -> skillProgress.find { it.nodeId == id } }

    /** Human-readable current counts for the selected node */
    fun currentCountsFor(node: SkillNode): Triple<Int, Int, Int> {
        val alarmHits = if (node.requiredAlarmIds.isNotEmpty()) {
            node.requiredAlarmIds.sumOf { historicalCompletions[it] ?: 0 }
        } else {
            when (node.pillar) {
                "AWAKENING" -> historicalCompletions["ignition"] ?: 0
                "TECHNE" -> listOf("deep_work_1", "deep_work_2", "commit")
                    .sumOf { historicalCompletions[it] ?: 0 }
                "HISTORIA" -> listOf("primary_source", "peripatetic")
                    .sumOf { historicalCompletions[it] ?: 0 }
                "GYMNOS" -> historicalCompletions["physical"] ?: 0
                "SOPHIA" -> listOf("digital_sunset", "scholar_compass")
                    .sumOf { historicalCompletions[it] ?: 0 }
                else -> 0
            }
        }
        return Triple(alarmHits, totalFocusSessions, journalDays)
    }
}

@HiltViewModel
class SkillTreeViewModel @Inject constructor(
    private val getSkillProgress: GetSkillProgressUseCase,
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _selectedNodeId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SkillTreeUiState> = combine(
        getSkillProgress(),
        repository.historicalCompletions,
        repository.totalFocusSessions,
        repository.journalEntries,
        _selectedNodeId
    ) { progress: List<SkillProgress>, historical: Map<String, Int>, focusSessions: Int, journalEntries: List<JournalEntry>, selectedId: String? ->
        SkillTreeUiState(
            skillProgress = progress,
            historicalCompletions = historical,
            totalFocusSessions = focusSessions,
            journalDays = journalEntries.map { it.date }.toSet().size,
            selectedNodeId = selectedId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SkillTreeUiState()
    )

    fun selectNode(nodeId: String?) {
        _selectedNodeId.value = nodeId
    }

    fun clearSelection() {
        _selectedNodeId.value = null
    }
}
