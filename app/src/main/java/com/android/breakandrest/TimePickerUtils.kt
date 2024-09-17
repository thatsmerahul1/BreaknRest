package com.android.breakandrest


import android.app.TimePickerDialog
import android.content.Context

fun showTimePicker(context: Context, initialTime: String, onTimeSelected: (String) -> Unit) {
    val parts = initialTime.split(":")
    val hour = parts[0].toInt()
    val minute = parts[1].toInt()

    TimePickerDialog(
        context,
        { _, selectedHour: Int, selectedMinute: Int ->
            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(time)
        },
        hour,
        minute,
        true
    ).show()
}