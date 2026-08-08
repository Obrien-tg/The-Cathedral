package com.example.thecathedral.data

import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val dataStoreManager: DataStoreManager) {

    val completedAlarms: Flow<Set<String>> = dataStoreManager.completedAlarms

    suspend fun markComplete(alarmId: String) {
        dataStoreManager.markAlarmComplete(alarmId)
    }

    suspend fun markIncomplete(alarmId: String) {
        dataStoreManager.markAlarmIncomplete(alarmId)
    }

    suspend fun clearAllProgress() {
        dataStoreManager.clearAllProgress()
    }
}
