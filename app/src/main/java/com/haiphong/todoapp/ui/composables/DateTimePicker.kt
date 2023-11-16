package com.haiphong.todoapp.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
private fun DatePicker(
    dialogState: MaterialDialogState,
    onDateChange: (LocalDate) -> Unit
) {
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(text = "Ok")
            negativeButton(text = "Cancel")
        }
    ) {
        datepicker(
            initialDate = LocalDate.now(),
            title = "Pick a date",
            allowedDateValidator = {
                it >= LocalDate.now()
            }
        ) {
            onDateChange(it)
        }
    }
}

@Composable
private fun TimePicker(
    dialogState: MaterialDialogState,
    date: LocalDate?,
    onTimeChange: (LocalTime) -> Unit
) {
    val initialTime = LocalTime.now().plusHours(1)
    val timeRange = if (date == null || date == LocalDate.now()) {
        initialTime..LocalTime.of(23, 59)
    } else {
        LocalTime.of(0, 0)..LocalTime.of(23, 59)
    }
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(text = "Ok")
            negativeButton(text = "Cancel")
        }
    ) {

        timepicker(
            initialTime = initialTime,
            is24HourClock = true,
            title = "Pick a time",
            timeRange = timeRange
        ) {
            onTimeChange(it)
        }
    }
}

@Composable
fun DateTimePicker(
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    date: LocalDate?,
    time: LocalTime?,
    modifier: Modifier = Modifier
) {
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(corner = CornerSize(24.dp)),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .width(120.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { dateDialogState.show() }) {
                    Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Date Picker")
                }
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (date != null) date.format(DateTimeFormatter.ofPattern("EEE, d MMM")) else "",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        Surface(
            shape = RoundedCornerShape(corner = CornerSize(24.dp)),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .width(120.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { timeDialogState.show() }) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Date Picker"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (time != null) time.format(DateTimeFormatter.ofPattern("HH:mm")) else "",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
    DatePicker(dialogState = dateDialogState, onDateChange = onDateChange)
    TimePicker(dialogState = timeDialogState, onTimeChange = onTimeChange, date = date)
}