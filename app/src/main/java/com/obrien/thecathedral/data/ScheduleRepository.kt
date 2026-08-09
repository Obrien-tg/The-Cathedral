package com.obrien.thecathedral.data

import com.obrien.thecathedral.model.JournalEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    val completedAlarms: Flow<Set<String>> = dataStoreManager.completedAlarms
    val journalEntries: Flow<List<JournalEntry>> = dataStoreManager.journalEntries
    val activeSourceIndex: Flow<Int> = dataStoreManager.activeSourceIndex
    val activeSourcePage: Flow<Int> = dataStoreManager.activeSourcePage
    val wakeTime: Flow<String?> = dataStoreManager.wakeTime

    suspend fun getLastResetDate(): String {
        return dataStoreManager.lastResetDate.first()
    }

    suspend fun setLastResetDate(date: String) {
        dataStoreManager.setLastResetDate(date)
    }

    suspend fun clearAlarmCompletionsOnly() {
        dataStoreManager.clearAlarmCompletionsOnly()
    }

    suspend fun checkDailyReset() {
        dataStoreManager.resetDailyIfNecessary()
    }

    suspend fun markComplete(id: String) {
        dataStoreManager.markComplete(id)
    }

    suspend fun markIncomplete(id: String) {
        dataStoreManager.markIncomplete(id)
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

    suspend fun setWakeTime(time: String) {
        dataStoreManager.setWakeTime(time)
    }

    suspend fun clearAllProgress() {
        dataStoreManager.clearAllProgress()
    }
}
