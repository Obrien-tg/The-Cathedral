package com.obrien.thecathedral.domain.usecase

import com.obrien.thecathedral.data.ScheduleData
import com.obrien.thecathedral.data.ScheduleRepository
import com.obrien.thecathedral.model.Pillar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject

class GetCurrentPillarsUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    operator fun invoke(): Flow<List<Pillar>> = repository.wakeTime.map { wakeTimeStr ->
        val wakeTime = try {
            LocalTime.parse(wakeTimeStr)
        } catch (_: Exception) {
            LocalTime.of(7, 0)
        }
        
        val baseWakeTime = LocalTime.of(7, 0)
        val offset = Duration.between(baseWakeTime, wakeTime)
        
        ScheduleData.pillars.map { pillar ->
            val shiftedAlarms = pillar.alarms.map { alarm ->
                alarm.copy(time = alarm.time.plus(offset))
            }
            pillar.copy(alarms = shiftedAlarms)
        }
    }
}
