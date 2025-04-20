package com.olehmaliuta.clothesadvisor.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DatePicker(
    selectedDate: Date?,
    onDateChanged: (Date) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Select Date",
    dateFormat: String = "yyyy-MM-dd"
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat(dateFormat, Locale.getDefault()) }

    val showDialog = remember { mutableStateOf(false) }

    Column {
        Button(onClick = { showDialog.value = true }) {
            Text(label)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Date: " + if (selectedDate != null)
                dateFormat.format(selectedDate) else "none",
            fontWeight = FontWeight.Bold
        )

        if (showDialog.value) {
            AndroidDatePickerDialog(
                initialDate = selectedDate,
                onDateSelected = { newDate ->
                    onDateChanged(newDate)
                    showDialog.value = false
                },
                onDismiss = { showDialog.value = false }
            )
        }
    }
}

@Composable
private fun AndroidDatePickerDialog(
    initialDate: Date?,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply {
        if (initialDate != null) {
            time = initialDate
        }
    }

    LocalLifecycleOwner.current.lifecycle.addObserver(
        object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                val datePickerDialog = android.app.DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        calendar.set(year, month, day)
                        onDateSelected(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.setOnDismissListener { onDismiss() }
                datePickerDialog.show()
            }
        }
    )
}