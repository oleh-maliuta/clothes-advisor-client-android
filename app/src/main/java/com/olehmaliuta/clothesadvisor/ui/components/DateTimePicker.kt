package com.olehmaliuta.clothesadvisor.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.olehmaliuta.clothesadvisor.App
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.ui.LanguageManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DateTimePicker(
    selectedDate: Date?,
    onDateSelected: (Date) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.date_time_picker__default_label)
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var tempDate by remember { mutableStateOf<Date?>(null) }

    val context = LocalContext.current
    val languageManager = (context.applicationContext as App).languageManager

    val dateFormatter = remember {
        SimpleDateFormat("MMM dd, yyyy", Locale(languageManager.getCurrentLanguage()))
    }
    val timeFormatter = remember {
        SimpleDateFormat("hh:mm:ss", Locale(languageManager.getCurrentLanguage()))
    }

    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Column(
            modifier = modifier
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { showDatePicker = true }
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = selectedDate?.let { dateFormatter.format(it) } ?:
                    stringResource(R.string.date_time_picker__select_date),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Select date"
                )
            }

            HorizontalDivider(
                thickness = 3.dp,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        if (selectedDate != null || tempDate != null) {
                            showTimePicker = true
                        }
                    }
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = when {
                        selectedDate != null -> timeFormatter.format(selectedDate)
                        tempDate != null -> stringResource(R.string.date_time_picker__select_time)
                        else -> stringResource(R.string.date_time_picker__select_date_then_time)
                    },
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Select time"
                )
            }
        }
    }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        selectedDate?.let { calendar.time = it }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            LocalContext.current,
            { _, selectedYear, selectedMonth, selectedDay ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(selectedYear, selectedMonth, selectedDay)
                tempDate = newCalendar.time
                showTimePicker = true
                showDatePicker = false
            },
            year,
            month,
            day
        )

        datePickerDialog.setButton(
            DatePickerDialog.BUTTON_NEGATIVE,
            stringResource(R.string.date_time_picker__cancel_button)
        ) { _, _ ->
            showDatePicker = false
        }

        datePickerDialog.show()
    }

    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        tempDate?.let { calendar.time = it } ?: selectedDate?.let { calendar.time = it }

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            LocalContext.current,
            { _, selectedHour, selectedMinute ->
                val newCalendar = Calendar.getInstance()
                tempDate?.let { newCalendar.time = it }
                newCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                newCalendar.set(Calendar.MINUTE, selectedMinute)

                val finalDate = newCalendar.time
                onDateSelected(finalDate)
                tempDate = null
                showTimePicker = false
            },
            hour,
            minute,
            false
        )

        timePickerDialog.setButton(
            TimePickerDialog.BUTTON_NEGATIVE,
            stringResource(R.string.date_time_picker__cancel_button)
        ) { _, _ ->
            showTimePicker = false
            tempDate = null
        }

        timePickerDialog.show()
    }
}