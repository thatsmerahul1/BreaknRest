package com.android.breakandrest


import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

class ProjectExtensions {
    val Context.dataStore by preferencesDataStore(name = "settings")
}