package com.obrien.thecathedral.domain.usecase

import com.obrien.thecathedral.model.Pillar
import com.obrien.thecathedral.util.isActiveAt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalTime
import javax.inject.Inject

class GetActivePillarUseCase @Inject constructor(
    private val getPersonalizedSchedule: GetPersonalizedScheduleUseCase
) {
    operator fun invoke(currentTime: Flow<LocalTime>): Flow<Pillar?> = combine(
        getPersonalizedSchedule(),
        currentTime
    ) { pillars, time ->
        pillars.find { it.isActiveAt(time) }
    }
}
