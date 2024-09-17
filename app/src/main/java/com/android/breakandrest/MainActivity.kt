package com.android.breakandrest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.android.breakandrest.ui.theme.StandUpReminderComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel = MainViewModel(application)

        setContent {
            StandUpReminderComposeTheme {
                val navController = rememberNavController()
                NavigationComponent(navController, viewModel)
            }
        }

        // Schedule reminders when the app starts
        viewModel.scheduleReminders()
    }
}

val Context.dataStore by preferencesDataStore(name = "settings")