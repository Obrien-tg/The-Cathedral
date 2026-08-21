package com.obrien.thecathedral.domain.usecase

import com.obrien.thecathedral.util.shiftTimeRange
import com.obrien.core.data.ScheduleRepository
import com.obrien.thecathedral.data.ScheduleShaper
import com.obrien.core.model.Pillar
import com.obrien.core.model.WeeklyIntention
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject

class GetPersonalizedScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    operator fun invoke(): Flow<List<Pillar>> = combine(
        repository.wakeTime,
        repository.weeklyIntention
    ) { wakeTimeStr: String?, intention: WeeklyIntention ->
        val wakeTime = try {
            LocalTime.parse(wakeTimeStr)
        } catch (_: Exception) {
            LocalTime.of(7, 0)
        }

        val baseWakeTime = LocalTime.of(7, 0)
        val offset = Duration.between(baseWakeTime, wakeTime)

        // 1. Shape the pillars based on weekly intentions
        val shapedPillars = ScheduleShaper.shape(intention)

        // 2. Apply time offset based on wake time
        shapedPillars.map { pillar ->
            val shiftedAlarms = pillar.alarms.map { alarm ->
                alarm.copy(time = alarm.time.plus(offset))
            }
            pillar.copy(
                alarms = shiftedAlarms,
                timeRange = pillar.shiftTimeRange(offset)
            )
        }
    }
}
