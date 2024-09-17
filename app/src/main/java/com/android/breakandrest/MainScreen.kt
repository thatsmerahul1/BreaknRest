package com.android.breakandrest

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun MainScreen(navController: NavHostController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val workHours by viewModel.workHours.collectAsState()

    var startTime by remember { mutableStateOf(workHours.startTime) }
    var endTime by remember { mutableStateOf(workHours.endTime) }

    val reminderInterval by viewModel.reminderInterval.collectAsState()
    var intervalText by remember { mutableStateOf(reminderInterval.toString()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Start Time:")
            Button(
                onClick = {
                    showTimePicker(context, startTime) { time ->
                        startTime = time
                    }
                }
            ) {
                Text(text = startTime)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "End Time:")
            Button(
                onClick = {
                    showTimePicker(context, endTime) { time ->
                        endTime = time
                    }
                }
            ) {
                Text(text = endTime)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val interval = intervalText.toIntOrNull()
                    if (interval == null || interval < 15) {
                        Toast.makeText(context, "Please enter a valid interval.", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.saveSettings(startTime, endTime, interval)
                        Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()
                        navController.navigateUp()
                    }
                }
            ) {
                Text(text = "Save")
            }
        }
    }
}
