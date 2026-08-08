package com.example.thecathedral.data

import com.example.thecathedral.model.JournalEntry
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val dataStoreManager: DataStoreManager) {

    val completedAlarms: Flow<Set<String>> = dataStoreManager.completedAlarms
    val journalEntries: Flow<List<JournalEntry>> = dataStoreManager.journalEntries
    val activeSourceIndex: Flow<Int> = dataStoreManager.activeSourceIndex
    val activeSourcePage: Flow<Int> = dataStoreManager.activeSourcePage

    suspend fun markComplete(alarmId: String) {
        dataStoreManager.markAlarmComplete(alarmId)
    }

    suspend fun markIncomplete(alarmId: String) {
        dataStoreManager.markAlarmIncomplete(alarmId)
    }

    suspend fun saveJournalEntry(entry: JournalEntry) {
        dataStoreManager.saveJournalEntry(entry)
    }

    suspend fun setActiveSource(index: Int) {
        dataStoreManager.setActiveSource(index)
    }

    suspend fun setActiveSourcePage(page: Int) {
        dataStoreManager.setActiveSourcePage(page)
    }

    suspend fun clearAllProgress() {
        dataStoreManager.clearAllProgress()
    }
}
