package com.android.breakandrest

import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val workHours by viewModel.workHours.collectAsState()
    val reminderInterval by viewModel.reminderInterval.collectAsState()

    var startTime by remember { mutableStateOf(workHours.startTime) }
    var endTime by remember { mutableStateOf(workHours.endTime) }
    var intervalText by remember { mutableStateOf(reminderInterval.toString()) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Start Time Picker
                Text(text = "Start Time:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    showTimePicker(context, startTime) { time ->
                        startTime = time
                    }
                }) {
                    Text(text = startTime)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // End Time Picker
                Text(text = "End Time:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    showTimePicker(context, endTime) { time ->
                        endTime = time
                    }
                }) {
                    Text(text = endTime)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reminder Interval Input
                Text(text = "Reminder Interval (minutes):", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = intervalText,
                    onValueChange = { intervalText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.width(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                Button(onClick = {
                    // Validate and save settings
                    val interval = intervalText.toIntOrNull()
                    if (interval == null || interval < 15) {
                        Toast.makeText(context, "Please enter an interval of at least 15 minutes.", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.saveSettings(startTime, endTime, interval)
                        Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()
                        navController.navigateUp()
                    }

                }) {
                    Text(text = "Save")
                }
            }
        }
    )
}