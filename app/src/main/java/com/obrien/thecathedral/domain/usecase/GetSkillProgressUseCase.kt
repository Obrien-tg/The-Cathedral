package com.obrien.thecathedral.domain.usecase

import com.obrien.thecathedral.data.ScheduleRepository
import com.obrien.thecathedral.model.SkillProgress
import com.obrien.thecathedral.model.SkillTreeData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSkillProgressUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    operator fun invoke(): Flow<List<SkillProgress>> = combine(
        repository.historicalCompletions,
        repository.totalFocusSessions,
        repository.journalEntries
    ) { historical, focusSessions, journalEntries ->
        val journalDays = journalEntries.map { it.date }.toSet().size

        // First pass – calculate raw progress for each node based on lifetime record
        val raw = SkillTreeData.nodes.associate { node ->
            val alarmHits = when (node.pillar) {
                "AWAKENING" -> historical["ignition"] ?: 0
                "TECHNE" -> listOf("deep_work_1", "deep_work_2", "commit")
                    .sumOf { historical[it] ?: 0 }
                "HISTORIA" -> listOf("primary_source", "peripatetic")
                    .sumOf { historical[it] ?: 0 }
                "GYMNOS" -> historical["physical"] ?: 0
                "SOPHIA" -> historical["digital_sunset"] ?: 0
                else -> 0
            }

            val completionRatio = (alarmHits.toFloat() / node.requiredCompletions.coerceAtLeast(1))
                .coerceAtMost(1f)
            val focusRatio = if (node.requiredFocusSessions > 0) {
                (focusSessions.toFloat() / node.requiredFocusSessions).coerceAtMost(1f)
            } else 1f
            val journalRatio = if (node.requiredJournalDays > 0) {
                (journalDays.toFloat() / node.requiredJournalDays).coerceAtMost(1f)
            } else 1f

            val overall = minOf(completionRatio, focusRatio, journalRatio)
            node.id to overall
        }

        // Second pass – determine unlock status (all parents must be completed)
        SkillTreeData.nodes.map { node ->
            val progress = raw[node.id] ?: 0f
            val completed = progress >= 1f

            val parents = SkillTreeData.edges.filter { it.to == node.id }.map { it.from }
            val unlocked = parents.isEmpty() || parents.all { parentId ->
                (raw[parentId] ?: 0f) >= 1f
            }

            SkillProgress(
                nodeId = node.id,
                unlocked = unlocked,
                completed = completed,
                progress = progress
            )
        }
    }
}
