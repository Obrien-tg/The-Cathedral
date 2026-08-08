package com.example.thecathedral.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.thecathedral.model.JournalEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cathedral_prefs")

class DataStoreManager(private val context: Context) {

    private val completedAlarmsKey = stringSetPreferencesKey("completed_alarms")
    private val journalKey = stringPreferencesKey("journal_entries")
    private val activeSourceIndexKey = intPreferencesKey("active_source_index")
    private val activeSourcePageKey = intPreferencesKey("active_source_page")

    // ─── Alarm Completion ───
    val completedAlarms: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[completedAlarmsKey] ?: emptySet()
    }

    suspend fun markAlarmComplete(alarmId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[completedAlarmsKey] ?: emptySet()
            prefs[completedAlarmsKey] = current + alarmId
        }
    }

    suspend fun markAlarmIncomplete(alarmId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[completedAlarmsKey] ?: emptySet()
            prefs[completedAlarmsKey] = current - alarmId
        }
    }

    // ─── Journal ───
    val journalEntries: Flow<List<JournalEntry>> = context.dataStore.data.map { prefs ->
        val json = prefs[journalKey] ?: "[]"
        try {
            Json.decodeFromString(json)
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun saveJournalEntry(entry: JournalEntry) {
        context.dataStore.edit { prefs ->
            val current = try {
                Json.decodeFromString<List<JournalEntry>>(prefs[journalKey] ?: "[]")
            } catch (_: Exception) {
                emptyList()
            }
            val updated = current.filter { it.date != entry.date } + entry
            prefs[journalKey] = Json.encodeToString(updated.sortedByDescending { it.timestamp })
        }
    }

    // ─── Primary Source Progress ───
    val activeSourceIndex: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[activeSourceIndexKey] ?: 0
    }

    val activeSourcePage: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[activeSourcePageKey] ?: 0
    }

    suspend fun setActiveSource(index: Int) {
        context.dataStore.edit { prefs ->
            prefs[activeSourceIndexKey] = index
            prefs[activeSourcePageKey] = 0
        }
    }

    suspend fun setActiveSourcePage(page: Int) {
        context.dataStore.edit { prefs ->
            prefs[activeSourcePageKey] = page
        }
    }

    suspend fun clearAllProgress() {
        context.dataStore.edit { prefs ->
            prefs.remove(completedAlarmsKey)
            prefs.remove(journalKey)
            prefs.remove(activeSourceIndexKey)
            prefs.remove(activeSourcePageKey)
        }
    }
}
