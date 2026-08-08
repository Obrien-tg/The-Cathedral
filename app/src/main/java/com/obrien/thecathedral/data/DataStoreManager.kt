package com.obrien.thecathedral.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.obrien.thecathedral.model.JournalEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

    val wakeTime: Flow<String?> = context.dataStore.data.map { it[WAKE_TIME] }

    val completedAlarms: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[COMPLETED_ALARMS] ?: emptySet()
    }

    val lastResetDate: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[LAST_RESET_DATE]
    }

    val journalEntries: Flow<List<JournalEntry>> = context.dataStore.data.map { prefs ->
        val strings = prefs[JOURNAL_ENTRIES] ?: emptySet()
        strings.map { Json.decodeFromString<JournalEntry>(it) }.sortedBy { it.date }
    }

    val activeSourceIndex: Flow<Int> = context.dataStore.data.map { it[ACTIVE_SOURCE_INDEX] ?: 0 }
    val activeSourcePage: Flow<Int> = context.dataStore.data.map { it[ACTIVE_SOURCE_PAGE] ?: 0 }

    suspend fun markComplete(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[COMPLETED_ALARMS] ?: emptySet()
            prefs[COMPLETED_ALARMS] = current + id
        }
    }

    suspend fun markIncomplete(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[COMPLETED_ALARMS] ?: emptySet()
            prefs[COMPLETED_ALARMS] = current - id
        }
    }

    suspend fun resetDailyIfNecessary() {
        val today = LocalDate.now().toString()
        context.dataStore.edit { prefs ->
            val lastReset = prefs[LAST_RESET_DATE]
            if (lastReset != today) {
                prefs[COMPLETED_ALARMS] = emptySet()
                prefs[LAST_RESET_DATE] = today
            }
        }
    }

    suspend fun saveJournalEntry(entry: JournalEntry) {
        context.dataStore.edit { prefs ->
            val current = prefs[JOURNAL_ENTRIES] ?: emptySet()
            val entryJson = Json.encodeToString(entry)
            // Filter out old entries for the same date
            val filtered = current.filter {
                val existing = Json.decodeFromString<JournalEntry>(it)
                existing.date != entry.date
            }
            prefs[JOURNAL_ENTRIES] = filtered.toSet() + entryJson
        }
    }

    suspend fun setActiveSource(index: Int) {
        context.dataStore.edit { it[ACTIVE_SOURCE_INDEX] = index }
    }

    suspend fun setActiveSourcePage(page: Int) {
        context.dataStore.edit { it[ACTIVE_SOURCE_PAGE] = page }
    }

    suspend fun setWakeTime(time: String) {
        context.dataStore.edit { it[WAKE_TIME] = time }
    }

    suspend fun clearAllProgress() {
        context.dataStore.edit { prefs ->
            prefs[COMPLETED_ALARMS] = emptySet()
            prefs[JOURNAL_ENTRIES] = emptySet()
            prefs[ACTIVE_SOURCE_INDEX] = 0
            prefs[ACTIVE_SOURCE_PAGE] = 0
        }
    }
}
