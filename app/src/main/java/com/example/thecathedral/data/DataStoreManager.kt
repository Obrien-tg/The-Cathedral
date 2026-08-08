package com.example.thecathedral.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cathedral_prefs")

class DataStoreManager(private val context: Context) {

    private val completedAlarmsKey = stringSetPreferencesKey("completed_alarms")

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

    suspend fun clearAllProgress() {
        context.dataStore.edit { prefs ->
            prefs.remove(completedAlarmsKey)
        }
    }
}
