package com.android.breakandrest

import android.app.Application
import android.util.Log
import androidx.work.Configuration

class MyApplication : Application(), Configuration.Provider {
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
}