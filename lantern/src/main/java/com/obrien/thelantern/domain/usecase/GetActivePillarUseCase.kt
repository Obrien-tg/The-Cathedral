package com.obrien.thelantern.domain.usecase

import com.obrien.core.model.Pillar
import com.obrien.thelantern.util.isActiveAt
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
