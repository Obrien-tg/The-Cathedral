package com.obrien.thecathedral.data

import com.obrien.thecathedral.model.JournalEntry
import com.obrien.thecathedral.model.WeeklyReview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val journalDao: JournalDao
) {
    val completedAlarms: Flow<Set<String>> = dataStoreManager.completedAlarms
    val skippedAlarms: Flow<Set<String>> = dataStoreManager.skippedAlarms
    val journalEntries: Flow<List<JournalEntry>> = journalDao.getAllEntriesFlow()
    val weeklyReviews: Flow<List<WeeklyReview>> = journalDao.getAllWeeklyReviewsFlow()
    val activeSourceIndex: Flow<Int> = dataStoreManager.activeSourceIndex
    val activeSourcePage: Flow<Int> = dataStoreManager.activeSourcePage
    val wakeTime: Flow<String?> = dataStoreManager.wakeTime
    val historicalCompletions: Flow<Map<String, Int>> = dataStoreManager.historicalCompletions
    val totalFocusSessions: Flow<Int> = dataStoreManager.totalFocusSessions
    val completionHistory: Flow<Map<String, Int>> = dataStoreManager.completionHistory
    val notificationLeadTime: Flow<Int> = dataStoreManager.notificationLeadTime
    val theme: Flow<String> = dataStoreManager.theme
    val fontSize: Flow<String> = dataStoreManager.fontSize
    val lastAccountabilityAcknowledgeDate: Flow<String> = dataStoreManager.lastAccountabilityAcknowledgeDate

    suspend fun getLastResetDate(): String = dataStoreManager.lastResetDate.first()
    suspend fun setLastResetDate(date: String) = dataStoreManager.setLastResetDate(date)
    suspend fun clearAlarmCompletionsOnly() = dataStoreManager.clearAlarmCompletionsOnly()
    suspend fun checkDailyReset() = dataStoreManager.resetDailyIfNecessary()

    suspend fun markComplete(id: String) = dataStoreManager.markComplete(id)
    suspend fun markIncomplete(id: String) = dataStoreManager.markIncomplete(id)
    suspend fun markSkipped(id: String) = dataStoreManager.markSkipped(id)
    suspend fun markUnskipped(id: String) = dataStoreManager.markUnskipped(id)
    suspend fun incrementHistoricalCompletion(alarmId: String) =
        dataStoreManager.incrementHistoricalCompletion(alarmId)
    suspend fun incrementFocusSessions() = dataStoreManager.incrementFocusSessions()

    suspend fun saveJournalEntry(entry: JournalEntry) {
        journalDao.insert(entry)
    }

    suspend fun saveWeeklyReview(review: WeeklyReview) {
        journalDao.insertWeeklyReview(review)
    }

    suspend fun getEntryByDate(date: String): JournalEntry? = journalDao.getEntryByDate(date)

    suspend fun setActiveSource(index: Int) = dataStoreManager.setActiveSource(index)
    suspend fun setActiveSourcePage(page: Int) = dataStoreManager.setActiveSourcePage(page)
    suspend fun setWakeTime(time: String) = dataStoreManager.setWakeTime(time)

    suspend fun setNotificationLeadTime(minutes: Int) = dataStoreManager.setNotificationLeadTime(minutes)
    suspend fun setTheme(theme: String) = dataStoreManager.setTheme(theme)
    suspend fun setFontSize(size: String) = dataStoreManager.setFontSize(size)
    suspend fun acknowledgeAccountability() = dataStoreManager.acknowledgeAccountability()
    
    suspend fun clearAllProgress() {
        dataStoreManager.clearAllProgress()
        // Should we clear Room too? User said daily reset should only clear alarm completions.
        // clearAllProgress is for the manual "Reset Day" button.
        // I'll keep Room journal entries as they are an "Archive".
    }
}
