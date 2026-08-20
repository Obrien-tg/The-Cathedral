package com.obrien.thecathedral.domain.usecase

import com.obrien.core.data.ScheduleRepository
import com.obrien.core.model.SkillNode
import com.obrien.core.model.SkillProgress
import com.obrien.thecathedral.model.SkillTreeData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Computes lifetime skill-tree progress from ritual history.
 *
 * Rules:
 * 1. Each node’s progress is the minimum of its three possible ratios
 *    (completions, focus sessions, journal days). All must reach 1.0.
 * 2. A node is unlocked only when every parent node is fully completed.
 * 3. Alarm IDs can be declared per-node; otherwise a sensible pillar default is used.
 */
class GetSkillProgressUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    operator fun invoke(): Flow<List<SkillProgress>> = combine(
        repository.historicalCompletions,
        repository.totalFocusSessions,
        repository.journalEntries
    ) { historical, focusSessions, journalEntries ->
        val journalDays = journalEntries.map { it.date }.toSet().size

        // First pass – raw progress for every node
        val raw: Map<String, Float> = SkillTreeData.nodes.associate { node ->
            node.id to computeProgress(node, historical, focusSessions, journalDays)
        }

        // Second pass – unlock status (all parents must be complete)
        SkillTreeData.nodes.map { node ->
            val progress = raw[node.id] ?: 0f
            val completed = progress >= 1f

            val parents = SkillTreeData.parentsOf(node.id)
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

    private fun computeProgress(
        node: SkillNode,
        historical: Map<String, Int>,
        focusSessions: Int,
        journalDays: Int
    ): Float {
        val alarmHits = resolveAlarmHits(node, historical)

        val completionRatio = (alarmHits.toFloat() / node.requiredCompletions.coerceAtLeast(1))
            .coerceIn(0f, 1f)

        val focusRatio = if (node.requiredFocusSessions > 0) {
            (focusSessions.toFloat() / node.requiredFocusSessions).coerceIn(0f, 1f)
        } else 1f

        val journalRatio = if (node.requiredJournalDays > 0) {
            (journalDays.toFloat() / node.requiredJournalDays).coerceIn(0f, 1f)
        } else 1f

        return minOf(completionRatio, focusRatio, journalRatio)
    }

    /**
     * Prefer the node’s explicit alarm list.
     * Fall back to the classic pillar → alarm mapping for backwards compatibility.
     */
    private fun resolveAlarmHits(node: SkillNode, historical: Map<String, Int>): Int {
        if (node.requiredAlarmIds.isNotEmpty()) {
            return node.requiredAlarmIds.sumOf { historical[it] ?: 0 }
        }

        return when (node.pillar) {
            "AWAKENING" -> historical["ignition"] ?: 0
            "TECHNE" -> listOf("deep_work_1", "deep_work_2", "commit")
                .sumOf { historical[it] ?: 0 }
            "HISTORIA" -> listOf("primary_source", "peripatetic")
                .sumOf { historical[it] ?: 0 }
            "GYMNOS" -> historical["physical"] ?: 0
            "SOPHIA" -> listOf("digital_sunset", "scholar_compass")
                .sumOf { historical[it] ?: 0 }
            else -> 0
        }
    }
}
