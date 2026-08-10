package com.obrien.thecathedral.domain.usecase

import com.obrien.thecathedral.data.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class DailyScore(
    val completedCount: Int,
    val totalCount: Int,
    val progress: Float
)

class GetDailyScoreUseCase @Inject constructor(
    private val repository: ScheduleRepository,
    private val getPersonalizedSchedule: GetPersonalizedScheduleUseCase
) {
    operator fun invoke(): Flow<DailyScore> = combine(
        getPersonalizedSchedule(),
        repository.completedAlarms
    ) { pillars, completedIds ->
        val allAlarms = pillars.flatMap { it.alarms }
        val total = allAlarms.size
        val completed = allAlarms.count { it.id in completedIds }
        DailyScore(
            completedCount = completed,
            totalCount = total,
            progress = if (total > 0) completed.toFloat() / total else 0f
        )
    }
}
