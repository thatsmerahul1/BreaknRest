package com.android.breakandrest

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkHoursRepository(application)

    private val _workHours = MutableStateFlow(WorkHours())
    val workHours: StateFlow<WorkHours> = _workHours

    private val _reminderInterval = MutableStateFlow(30) // Default to 30 minutes
    val reminderInterval: StateFlow<Int> = _reminderInterval


    private val _remindersEnabled = MutableStateFlow(true)
    val remindersEnabled: StateFlow<Boolean> = _remindersEnabled

    init {
        loadSettings()
    }

    fun saveSettings(startTime: String, endTime: String, interval: Int) {
        _workHours.value = WorkHours(startTime, endTime)
        _reminderInterval.value = interval
        viewModelScope.launch {
            repository.saveSettings(_workHours.value, interval)
        }
        scheduleReminders()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val settings = repository.getSettings()
            _workHours.value = settings.workHours
            _reminderInterval.value = settings.reminderInterval
        }
    }

    fun scheduleReminders() {
        val context = getApplication<Application>().applicationContext
        WorkManager.getInstance(context).cancelUniqueWork("StandUpReminderWork")

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val intervalMinutes = _reminderInterval.value.toLong()
        val initialDelayMinutes = calculateInitialDelay(intervalMinutes)

        val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(intervalMinutes, TimeUnit.MINUTES)
            .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "StandUpReminderWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            reminderRequest
        )
    }



    fun cancelReminders() {
        val context = getApplication<Application>().applicationContext
        WorkManager.getInstance(context).cancelUniqueWork("StandUpReminderWork")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateInitialDelay(intervalMinutes: Long): Long {
        val now = LocalDateTime.now()
        val nextReminderTime = now.plusMinutes(intervalMinutes - (now.minute % intervalMinutes))
        val duration = Duration.between(now, nextReminderTime)
        return duration.toMinutes()
    }


    fun setRemindersEnabled(enabled: Boolean) {
        _remindersEnabled.value = enabled
        if (enabled) {
            scheduleReminders()
        } else {
            cancelReminders()
        }
    }
}