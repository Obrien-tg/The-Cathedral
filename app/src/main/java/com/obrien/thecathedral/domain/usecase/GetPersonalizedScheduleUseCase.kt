package com.obrien.thecathedral.domain.usecase

import com.obrien.core.data.ScheduleRepository
import com.obrien.core.model.Pillar
import com.obrien.core.model.WeeklyIntention
import com.obrien.thecathedral.data.DayScheduler
import com.obrien.thecathedral.data.ScheduleShaper
import com.obrien.thecathedral.util.shiftTimeRange
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

        if (DayScheduler.hasStructuralPlan(intention)) {
            when (val result = DayScheduler.build(intention)) {
                // Declared envelope + focus hours: compute the day structure.
                is DayScheduler.Result.Schedule -> result.pillars
                // Refuse to build an impossible day; fall back to the
                // classic offset path rather than a broken schedule.
                is DayScheduler.Result.Invalid -> offsetFromWake(wakeTimeStr, intention)
            }
        } else {
            // No structural vows yet: legacy behaviour (uniform wake offset).
            offsetFromWake(wakeTimeStr, intention)
        }
    }

    private fun offsetFromWake(wakeTimeStr: String?, intention: WeeklyIntention): List<Pillar> {
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
        return shapedPillars.map { pillar ->
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
