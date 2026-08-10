package com.obrien.thecathedral.domain.usecase

import com.obrien.thecathedral.data.ScheduleRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleRitualUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(alarmId: String) {
        val completedIds = repository.completedAlarms.first()
        if (alarmId in completedIds) {
            repository.markIncomplete(alarmId)
        } else {
            repository.markComplete(alarmId)
            repository.incrementHistoricalCompletion(alarmId)
        }
    }
}
