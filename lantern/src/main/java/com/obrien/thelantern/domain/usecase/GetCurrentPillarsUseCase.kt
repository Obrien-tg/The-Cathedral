package com.obrien.thelantern.domain.usecase

import com.obrien.thelantern.data.ScheduleData
import com.obrien.thelantern.data.ScheduleShaper
import com.obrien.core.data.ScheduleRepository
import com.obrien.core.model.Pillar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject

class GetCurrentPillarsUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    operator fun invoke(): Flow<List<Pillar>> = combine(
        repository.wakeTime,
        repository.weeklyIntention
    ) { wakeTimeStr, intention ->
        val wakeTime = try {
            LocalTime.parse(wakeTimeStr)
        } catch (_: Exception) {
            LocalTime.of(6, 30)
        }
        
        val baseWakeTime = LocalTime.of(6, 30)
        val offset = Duration.between(baseWakeTime, wakeTime)
        
        val shiftedPillars = ScheduleData.pillars.map { pillar ->
            val shiftedAlarms = pillar.alarms.map { alarm ->
                alarm.copy(time = alarm.time.plus(offset))
            }
            pillar.copy(alarms = shiftedAlarms)
        }

        ScheduleShaper.shape(intention, shiftedPillars)
    }
}
