package com.android.breakandrest
import androidx.compose.animation.animateContentSize
import androidx.annotation.RequiresApi
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(navController: NavController, viewModel: MainViewModel) {
    val workHours by viewModel.workHours.collectAsState()
    val remindersEnabled by viewModel.remindersEnabled.collectAsState()
    val reminderInterval by viewModel.reminderInterval.collectAsState()
    var nextReminderIn by remember { mutableStateOf(Duration.ZERO) }

    LaunchedEffect(reminderInterval) {
        while (true) {
            nextReminderIn = calculateNextReminder(reminderInterval)
            delay(1000L) // Update every second
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Stand Up Reminder") },
                actions = {
                    IconButton(onClick = { /* TODO: Implement profile actions */ }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
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
                    .fillMaxSize()
                    .animateContentSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Work Hours",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Start: ${workHours.startTime}")
                Text(text = "End: ${workHours.endTime}")

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Next Reminder in: ${formatDuration(nextReminderIn)}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (remindersEnabled) "Reminders On" else "Reminders Off")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = remindersEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.setRemindersEnabled(enabled)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navController.navigate("settings") }) {
                    Text(text = "Edit Work Hours")
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateNextReminder(reminderInterval: Int): Duration {
    val now = LocalTime.now()
    val intervalMinutes = reminderInterval.toLong()
    val nextReminderMinute = ((now.minute / intervalMinutes) + 1) * intervalMinutes
    val nextReminderTime = LocalTime.of(now.hour, 0).plusMinutes(nextReminderMinute)
    if (nextReminderTime.isBefore(now)) {
        // If the calculated time is before now, add one hour
        nextReminderTime.plusHours(1)
    }
    return Duration.between(now, nextReminderTime)
}
@RequiresApi(Build.VERSION_CODES.O)
fun formatDuration(duration: Duration): String {
    val totalSeconds = duration.seconds
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}