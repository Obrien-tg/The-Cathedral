package com.obrien.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.obrien.core.model.JournalEntry
import com.obrien.core.model.WeeklyIntention
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.LocalDate

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "formation_prefs")

class DataStoreManager(private val context: Context, private val prefsName: String = "formation_prefs") {

    companion object {
        val COMPLETED_ALARMS = stringSetPreferencesKey("completed_alarms")
        val SKIPPED_ALARMS = stringSetPreferencesKey("skipped_alarms")
        val LAST_RESET_DATE = stringPreferencesKey("last_reset_date")
        val ACTIVE_SOURCE_INDEX = intPreferencesKey("active_source_index")
        val ACTIVE_SOURCE_PAGE = intPreferencesKey("active_source_page")
        val WAKE_TIME = stringPreferencesKey("wake_time")
        val HISTORICAL_COMPLETIONS = stringPreferencesKey("historical_completions")
        val TOTAL_FOCUS_SESSIONS = intPreferencesKey("total_focus_sessions")
        val COMPLETION_HISTORY = stringPreferencesKey("completion_history")
        val NOTIFICATION_LEAD_TIME = intPreferencesKey("notification_lead_time")
        val THEME = stringPreferencesKey("theme")
        val FONT_SIZE = stringPreferencesKey("font_size")
        val LAST_ACCOUNTABILITY_ACKNOWLEDGE_DATE = stringPreferencesKey("last_accountability_acknowledge_date")
        val WEEKLY_INTENTION = stringPreferencesKey("weekly_intention")
    }

    val lastAccountabilityAcknowledgeDate: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[LAST_ACCOUNTABILITY_ACKNOWLEDGE_DATE] ?: "" }

    suspend fun acknowledgeAccountability() {
        try {
            val today = LocalDate.now().toString()
            context.dataStore.edit { it[LAST_ACCOUNTABILITY_ACKNOWLEDGE_DATE] = today }
        } catch (e: Exception) { e.printStackTrace() }
    }

    val notificationLeadTime: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[NOTIFICATION_LEAD_TIME] ?: 5 }

    val theme: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[THEME] ?: "dark" }

    val fontSize: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[FONT_SIZE] ?: "medium" }

    val weeklyIntention: Flow<WeeklyIntention> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            val json = prefs[WEEKLY_INTENTION]
                ?: return@map WeeklyIntention.emptyForCurrentWeek()
            try {
                val decoded = Json.decodeFromString<WeeklyIntention>(json)
                if (decoded.isActiveForCurrentWeek()) decoded
                else WeeklyIntention.emptyForCurrentWeek()
            } catch (_: Exception) {
                WeeklyIntention.emptyForCurrentWeek()
            }
        }

    fun currentWeekStart(): String {
        val today = LocalDate.now()
        val monday = today.minusDays((today.dayOfWeek.value - 1).toLong())
        return monday.toString() // yyyy-MM-dd
    }

    suspend fun setNotificationLeadTime(minutes: Int) {
        try {
            context.dataStore.edit { it[NOTIFICATION_LEAD_TIME] = minutes }
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun setTheme(theme: String) {
        try {
            context.dataStore.edit { it[THEME] = theme }
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun setFontSize(size: String) {
        try {
            context.dataStore.edit { it[FONT_SIZE] = size }
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun saveWeeklyIntention(intention: WeeklyIntention) {
        try {
            context.dataStore.edit { it[WEEKLY_INTENTION] = Json.encodeToString(intention) }
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun clearWeeklyIntention() {
        try {
            context.dataStore.edit { it.remove(WEEKLY_INTENTION) }
        } catch (e: Exception) { e.printStackTrace() }
    }

    val wakeTime: Flow<String?> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[WAKE_TIME] }

    val completedAlarms: Flow<Set<String>> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            prefs[COMPLETED_ALARMS] ?: emptySet()
        }

    val skippedAlarms: Flow<Set<String>> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[SKIPPED_ALARMS] ?: emptySet() }

    val lastResetDate: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            prefs[LAST_RESET_DATE] ?: ""
        }

    val activeSourceIndex: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[ACTIVE_SOURCE_INDEX] ?: 0 }

    val activeSourcePage: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[ACTIVE_SOURCE_PAGE] ?: 0 }

    // Lifetime record of every ritual completed — the true measure of ascent
    val historicalCompletions: Flow<Map<String, Int>> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            val json = prefs[HISTORICAL_COMPLETIONS] ?: "{}"
            try {
                Json.decodeFromString<Map<String, Int>>(json)
            } catch (_: Exception) {
                emptyMap()
            }
        }

    val totalFocusSessions: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[TOTAL_FOCUS_SESSIONS] ?: 0 }

    // Map of date string to number of rituals completed that day
    val completionHistory: Flow<Map<String, Int>> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            val json = prefs[COMPLETION_HISTORY] ?: "{}"
            try {
                Json.decodeFromString<Map<String, Int>>(json)
            } catch (_: Exception) {
                emptyMap()
            }
        }

    suspend fun markComplete(id: String) {
        try {
            val today = LocalDate.now().toString()
            context.dataStore.edit { prefs ->
                val current = prefs[COMPLETED_ALARMS] ?: emptySet()
                if (id !in current) {
                    prefs[COMPLETED_ALARMS] = current + id
                    
                    // Update daily completion count for heatmap
                    val historyJson = prefs[COMPLETION_HISTORY] ?: "{}"
                    val history = try {
                        Json.decodeFromString<Map<String, Int>>(historyJson).toMutableMap()
                    } catch (_: Exception) {
                        mutableMapOf()
                    }
                    history[today] = (history[today] ?: 0) + 1
                    prefs[COMPLETION_HISTORY] = Json.encodeToString(history)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun markIncomplete(id: String) {
        try {
            val today = LocalDate.now().toString()
            context.dataStore.edit { prefs ->
                val current = prefs[COMPLETED_ALARMS] ?: emptySet()
                if (id in current) {
                    prefs[COMPLETED_ALARMS] = current - id
                    
                    // Decrement daily completion count
                    val historyJson = prefs[COMPLETION_HISTORY] ?: "{}"
                    val history = try {
                        Json.decodeFromString<Map<String, Int>>(historyJson).toMutableMap()
                    } catch (_: Exception) {
                        mutableMapOf()
                    }
                    val count = (history[today] ?: 0) - 1
                    if (count <= 0) history.remove(today) else history[today] = count
                    prefs[COMPLETION_HISTORY] = Json.encodeToString(history)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun markSkipped(id: String) {
        try {
            context.dataStore.edit { prefs ->
                val current = prefs[SKIPPED_ALARMS] ?: emptySet()
                prefs[SKIPPED_ALARMS] = current + id
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun markUnskipped(id: String) {
        try {
            context.dataStore.edit { prefs ->
                val current = prefs[SKIPPED_ALARMS] ?: emptySet()
                prefs[SKIPPED_ALARMS] = current - id
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun incrementHistoricalCompletion(alarmId: String) {
        try {
            context.dataStore.edit { prefs ->
                val current = try {
                    Json.decodeFromString<Map<String, Int>>(prefs[HISTORICAL_COMPLETIONS] ?: "{}")
                } catch (_: Exception) {
                    emptyMap()
                }
                val updated = current.toMutableMap()
                updated[alarmId] = (updated[alarmId] ?: 0) + 1
                prefs[HISTORICAL_COMPLETIONS] = Json.encodeToString(updated)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun incrementFocusSessions() {
        try {
            context.dataStore.edit { prefs ->
                val current = prefs[TOTAL_FOCUS_SESSIONS] ?: 0
                prefs[TOTAL_FOCUS_SESSIONS] = current + 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun setLastResetDate(date: String) {
        try {
            context.dataStore.edit { it[LAST_RESET_DATE] = date }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun clearAlarmCompletionsOnly() {
        try {
            context.dataStore.edit { it.remove(COMPLETED_ALARMS) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun resetDailyIfNecessary() {
        try {
            val today = LocalDate.now().toString()
            context.dataStore.edit { prefs ->
                val lastReset = prefs[LAST_RESET_DATE]
                if (lastReset != today) {
                    prefs[COMPLETED_ALARMS] = emptySet()
                    prefs[SKIPPED_ALARMS] = emptySet()
                    prefs[LAST_RESET_DATE] = today
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun setActiveSource(index: Int) {
        try {
            context.dataStore.edit { it[ACTIVE_SOURCE_INDEX] = index }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun setActiveSourcePage(page: Int) {
        try {
            context.dataStore.edit { it[ACTIVE_SOURCE_PAGE] = page }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun setWakeTime(time: String) {
        try {
            context.dataStore.edit { it[WAKE_TIME] = time }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun clearAllProgress() {
        try {
            context.dataStore.edit { prefs ->
                prefs[COMPLETED_ALARMS] = emptySet()
                prefs[ACTIVE_SOURCE_INDEX] = 0
                prefs[ACTIVE_SOURCE_PAGE] = 0
                // We deliberately do NOT clear historicalCompletions or focus sessions.
                // The record of a man’s labour is not to be erased lightly.
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
