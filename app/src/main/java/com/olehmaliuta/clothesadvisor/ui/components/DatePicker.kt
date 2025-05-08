package com.olehmaliuta.clothesadvisor.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DatePicker(
    selectedDate: Date?,
    onDateSelected: (Date?) -> Unit,  // Changed to accept null
    modifier: Modifier = Modifier,
    label: String = "Select date",
    showClearButton: Boolean = true  // Optional parameter to show/hide clear button
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Column(modifier = modifier) {
        Text(
            text = label,
            modifier = Modifier.padding(bottom = 4.dp),
            style = MaterialTheme.typography.bodySmall
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    ),
                    shape = MaterialTheme.shapes.small
                )
                .fillMaxWidth()
        ) {
            Text(
                text = selectedDate?.let { dateFormatter.format(it) } ?: "Not selected",
                modifier = Modifier
                    .weight(1f)
                    .clickable { showDatePicker = true }
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                color = if (selectedDate == null) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.onSurface
            )

            if (showClearButton && selectedDate != null) {
                IconButton(
                    onClick = { onDateSelected(null) },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear date",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            IconButton(
                onClick = { showDatePicker = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Select date",
                    tint = MaterialTheme.colorScheme.primary
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
                onDateSelected(newCalendar.time)
                showDatePicker = false
            },
            year,
            month,
            day
        )

        // Add a neutral button to explicitly set null
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Clear") { _, _ ->
            onDateSelected(null)
            showDatePicker = false
        }

        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
            showDatePicker = false
        }

        datePickerDialog.show()
    }
}