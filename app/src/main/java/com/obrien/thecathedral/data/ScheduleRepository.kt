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

    suspend fun clearAllProgress() {
        dataStoreManager.clearAllProgress()
    }
}
