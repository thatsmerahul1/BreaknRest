package com.android.breakandrest

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

data class UserSettings(
    val workHours: WorkHours,
    val reminderInterval: Int
)

class WorkHoursRepository(private val context: Context) {

    private val START_TIME_KEY = stringPreferencesKey("startTime")
    private val END_TIME_KEY = stringPreferencesKey("endTime")
    private val INTERVAL_KEY = intPreferencesKey("reminderInterval")

    suspend fun saveSettings(workHours: WorkHours, reminderInterval: Int) {
        context.dataStore.edit { preferences ->
            preferences[START_TIME_KEY] = workHours.startTime
            preferences[END_TIME_KEY] = workHours.endTime
            preferences[INTERVAL_KEY] = reminderInterval
        }
    }

    suspend fun getSettings(): UserSettings {
        val preferences = context.dataStore.data.first()
        val startTime = preferences[START_TIME_KEY] ?: "09:00"
        val endTime = preferences[END_TIME_KEY] ?: "17:30"
        val interval = preferences[INTERVAL_KEY] ?: 30
        return UserSettings(WorkHours(startTime, endTime), interval)
    }
}