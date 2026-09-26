package com.obrien.core.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.obrien.core.model.WeeklyIntention
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "formation_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        private const val TAG = "DataStoreManager"
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
        val PILLAR_SCHEDULE = stringPreferencesKey("pillar_schedule")

        // Tutorial & Day Personalization
        val HAS_SEEN_TUTORIAL = booleanPreferencesKey("has_seen_tutorial")
        val MONDAY_COLOR = stringPreferencesKey("monday_color")
        val TUESDAY_COLOR = stringPreferencesKey("tuesday_color")
        val WEDNESDAY_COLOR = stringPreferencesKey("wednesday_color")
        val THURSDAY_COLOR = stringPreferencesKey("thursday_color")
        val FRIDAY_COLOR = stringPreferencesKey("friday_color")
        val SATURDAY_COLOR = stringPreferencesKey("saturday_color")
        val SUNDAY_COLOR = stringPreferencesKey("sunday_color")
    }

    val hasSeenTutorial: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[HAS_SEEN_TUTORIAL] ?: false }

    suspend fun setTutorialSeen() {
        try {
            context.dataStore.edit { it[HAS_SEEN_TUTORIAL] = true }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to set tutorial seen", e)
        }
    }

    fun getTodayColor(): Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            val today = LocalDate.now().dayOfWeek.name
            val key = when (today) {
                "MONDAY" -> MONDAY_COLOR
                "TUESDAY" -> TUESDAY_COLOR
                "WEDNESDAY" -> WEDNESDAY_COLOR
                "THURSDAY" -> THURSDAY_COLOR
                "FRIDAY" -> FRIDAY_COLOR
                "SATURDAY" -> SATURDAY_COLOR
                "SUNDAY" -> SUNDAY_COLOR
                else -> MONDAY_COLOR
            }
            prefs[key] ?: getDefaultColorForDay(today)
        }

    suspend fun saveDayColor(day: String, colorHex: String) {
        try {
            val key = when (day.uppercase()) {
                "MONDAY" -> MONDAY_COLOR
                "TUESDAY" -> TUESDAY_COLOR
                "WEDNESDAY" -> WEDNESDAY_COLOR
                "THURSDAY" -> THURSDAY_COLOR
                "FRIDAY" -> FRIDAY_COLOR
                "SATURDAY" -> SATURDAY_COLOR
                "SUNDAY" -> SUNDAY_COLOR
                else -> MONDAY_COLOR
            }
            context.dataStore.edit { it[key] = colorHex }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to save day color for $day", e)
        }
    }

    private fun getDefaultColorForDay(day: String): String = when (day.uppercase()) {
        "MONDAY" -> "FFB3C6"
        "TUESDAY" -> "C4B5FD"
        "WEDNESDAY" -> "A5D8FF"
        "THURSDAY" -> "B5EAD7"
        "FRIDAY" -> "FFD6A5"
        "SATURDAY" -> "FFF3B0"
        "SUNDAY" -> "D8B4FE"
        else -> "FFB3C6"
    }

    val lastAccountabilityAcknowledgeDate: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[LAST_ACCOUNTABILITY_ACKNOWLEDGE_DATE] ?: "" }

    suspend fun acknowledgeAccountability() {
        try {
            val today = LocalDate.now().toString()
            context.dataStore.edit { it[LAST_ACCOUNTABILITY_ACKNOWLEDGE_DATE] = today }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to acknowledge accountability", e)
        }
    }

    val notificationLeadTime: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[NOTIFICATION_LEAD_TIME] ?: 5 }

    val theme: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[THEME] ?: "system" } // "system", "dark", "light"

    val fontSize: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[FONT_SIZE] ?: "medium" }

    /**
     * Raw decoded intention, regardless of week. Used by the Quest of the Week
     * planning screen, which must see both the current week's quests and the
     * upcoming week's quests saved on Sunday. Schedule shaping must NOT use
     * this flow — use [weeklyIntention], which filters to the active week.
     */
    val weeklyIntentionRaw: Flow<WeeklyIntention> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            val json = prefs[WEEKLY_INTENTION]
                ?: return@map WeeklyIntention.emptyForCurrentWeek()
            try {
                Json.decodeFromString<WeeklyIntention>(json)
            } catch (_: Exception) {
                WeeklyIntention.emptyForCurrentWeek()
            }
        }

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
        return monday.toString()
    }

    suspend fun setNotificationLeadTime(minutes: Int) {
        try {
            context.dataStore.edit { it[NOTIFICATION_LEAD_TIME] = minutes }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to set notification lead time", e)
        }
    }

    suspend fun setTheme(theme: String) {
        require(theme in setOf("system", "dark", "light")) { "Invalid theme: $theme" }
        try {
            context.dataStore.edit { it[THEME] = theme }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to set theme", e)
        }
    }

    suspend fun setFontSize(size: String) {
        require(size in setOf("small", "medium", "large")) { "Invalid font size: $size" }
        try {
            context.dataStore.edit { it[FONT_SIZE] = size }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to set font size", e)
        }
    }

    suspend fun saveWeeklyIntention(intention: WeeklyIntention) {
        try {
            context.dataStore.edit { it[WEEKLY_INTENTION] = Json.encodeToString(intention) }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to save weekly intention", e)
        }
    }

    suspend fun clearWeeklyIntention() {
        try {
            context.dataStore.edit { it.remove(WEEKLY_INTENTION) }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to clear weekly intention", e)
        }
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

    // Lifetime record of every ritual completed
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
        } catch (e: IOException) {
            Log.e(TAG, "Failed to mark complete: $id", e)
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
        } catch (e: IOException) {
            Log.e(TAG, "Failed to mark incomplete: $id", e)
        }
    }

    suspend fun markSkipped(id: String) {
        try {
            context.dataStore.edit { prefs ->
                val current = prefs[SKIPPED_ALARMS] ?: emptySet()
                prefs[SKIPPED_ALARMS] = current + id
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to mark skipped: $id", e)
        }
    }

    suspend fun markUnskipped(id: String) {
        try {
            context.dataStore.edit { prefs ->
                val current = prefs[SKIPPED_ALARMS] ?: emptySet()
                prefs[SKIPPED_ALARMS] = current - id
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to mark unskipped: $id", e)
        }
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
        } catch (e: IOException) {
            Log.e(TAG, "Failed to increment historical completion: $alarmId", e)
        }
    }

    suspend fun incrementFocusSessions() {
        try {
            context.dataStore.edit { prefs ->
                val current = prefs[TOTAL_FOCUS_SESSIONS] ?: 0
                prefs[TOTAL_FOCUS_SESSIONS] = current + 1
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to increment focus sessions", e)
        }
    }

    suspend fun setLastResetDate(date: String) {
        try {
            context.dataStore.edit { it[LAST_RESET_DATE] = date }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to set last reset date", e)
        }
    }

    suspend fun clearAlarmCompletionsOnly() {
        try {
            context.dataStore.edit { it.remove(COMPLETED_ALARMS) }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to clear alarm completions", e)
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
        } catch (e: IOException) {
            Log.e(TAG, "Failed to reset daily", e)
        }
    }

    suspend fun setActiveSource(index: Int) {
        try {
            context.dataStore.edit { it[ACTIVE_SOURCE_INDEX] = index }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to set active source", e)
        }
    }

    suspend fun setActiveSourcePage(page: Int) {
        try {
            context.dataStore.edit { it[ACTIVE_SOURCE_PAGE] = page }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to set active source page", e)
        }
    }

    suspend fun setWakeTime(time: String) {
        // Validate time format (HH:MM)
        try {
            LocalTime.parse(time)
        } catch (e: DateTimeParseException) {
            Log.e(TAG, "Invalid wake time format: $time. Expected HH:MM")
            return
        }
        try {
            context.dataStore.edit { it[WAKE_TIME] = time }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to set wake time", e)
        }
    }

    suspend fun savePillarSchedule(scheduleJson: String) {
        try {
            context.dataStore.edit { it[PILLAR_SCHEDULE] = scheduleJson }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to save pillar schedule", e)
        }
    }

    val pillarSchedule: Flow<String?> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[PILLAR_SCHEDULE] }

    suspend fun clearAllProgress() {
        try {
            context.dataStore.edit { prefs ->
                prefs[COMPLETED_ALARMS] = emptySet()
                prefs[ACTIVE_SOURCE_INDEX] = 0
                prefs[ACTIVE_SOURCE_PAGE] = 0
                // Historical completions and focus sessions are intentionally preserved.
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to clear all progress", e)
        }
    }
}
