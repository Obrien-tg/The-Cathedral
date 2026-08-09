package com.obrien.thecathedral.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.obrien.thecathedral.model.JournalEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.LocalDate

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cathedral_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val COMPLETED_ALARMS = stringSetPreferencesKey("completed_alarms")
        val LAST_RESET_DATE = stringPreferencesKey("last_reset_date")
        val JOURNAL_ENTRIES = stringSetPreferencesKey("journal_entries")
        val ACTIVE_SOURCE_INDEX = intPreferencesKey("active_source_index")
        val ACTIVE_SOURCE_PAGE = intPreferencesKey("active_source_page")
        val WAKE_TIME = stringPreferencesKey("wake_time")
    }

    val wakeTime: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[WAKE_TIME] }

    val completedAlarms: Flow<Set<String>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            prefs[COMPLETED_ALARMS] ?: emptySet()
        }

    val lastResetDate: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            prefs[LAST_RESET_DATE] ?: ""
        }

    val journalEntries: Flow<List<JournalEntry>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            try {
                val strings = prefs[JOURNAL_ENTRIES] ?: emptySet()
                strings.map { Json.decodeFromString<JournalEntry>(it) }.sortedBy { it.date }
            } catch (e: Exception) {
                emptyList()
            }
        }

    val activeSourceIndex: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[ACTIVE_SOURCE_INDEX] ?: 0 }

    val activeSourcePage: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[ACTIVE_SOURCE_PAGE] ?: 0 }

    suspend fun markComplete(id: String) {
        try {
            context.dataStore.edit { prefs ->
                val current = prefs[COMPLETED_ALARMS] ?: emptySet()
                prefs[COMPLETED_ALARMS] = current + id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun markIncomplete(id: String) {
        try {
            context.dataStore.edit { prefs ->
                val current = prefs[COMPLETED_ALARMS] ?: emptySet()
                prefs[COMPLETED_ALARMS] = current - id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun setLastResetDate(date: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[LAST_RESET_DATE] = date
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun clearAlarmCompletionsOnly() {
        try {
            context.dataStore.edit { prefs ->
                prefs.remove(COMPLETED_ALARMS)
            }
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
                    prefs[LAST_RESET_DATE] = today
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveJournalEntry(entry: JournalEntry) {
        try {
            context.dataStore.edit { prefs ->
                val current = prefs[JOURNAL_ENTRIES] ?: emptySet()
                val entryJson = Json.encodeToString(entry)
                val filtered = current.filter {
                    try {
                        val existing = Json.decodeFromString<JournalEntry>(it)
                        existing.date != entry.date
                    } catch (e: Exception) {
                        false
                    }
                }
                prefs[JOURNAL_ENTRIES] = filtered.toSet() + entryJson
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
                prefs[JOURNAL_ENTRIES] = emptySet()
                prefs[ACTIVE_SOURCE_INDEX] = 0
                prefs[ACTIVE_SOURCE_PAGE] = 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
