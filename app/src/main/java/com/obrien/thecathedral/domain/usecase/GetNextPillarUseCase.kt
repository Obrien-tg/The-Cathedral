package com.obrien.thecathedral.domain.usecase

import com.obrien.core.model.Pillar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalTime
import javax.inject.Inject

class GetNextPillarUseCase @Inject constructor(
    private val getPersonalizedSchedule: GetPersonalizedScheduleUseCase
) {
    operator fun invoke(currentTime: Flow<LocalTime>): Flow<Pillar?> = combine(
        getPersonalizedSchedule(),
        currentTime
    ) { pillars, time ->
        pillars.find { pillar ->
            pillar.alarms.firstOrNull()?.time?.isAfter(time) == true
        }
    }
}
