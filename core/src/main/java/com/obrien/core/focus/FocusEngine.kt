package com.obrien.core.focus

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Duration

object FocusEngine {
    fun countdown(minutes: Int): Flow<Int> = flow {
        var remaining = minutes * 60
        while (remaining >= 0) {
            emit(remaining)
            delay(1000)
            remaining--
        }
    }
}
