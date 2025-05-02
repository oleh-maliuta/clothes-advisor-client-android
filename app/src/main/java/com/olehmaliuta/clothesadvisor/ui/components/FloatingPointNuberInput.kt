package com.olehmaliuta.clothesadvisor.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun FloatingPointNumberInput(
    value: Double?,
    onValueChange: (Double?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Amount"
) {
    var text by remember(value) {
        mutableStateOf(value?.let { "%.2f".format(it) } ?: "")
    }

    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            if (newText.isEmpty()) {
                text = ""
                onValueChange(null)
                return@OutlinedTextField
            }

            if (newText.matches(Regex("^\\d*\\.?\\d*$"))) {
                val parts = newText.split('.')

                if (parts.size == 2 && parts[1].length > 2) {
                    return@OutlinedTextField
                }

                text = newText

                runCatching {
                    onValueChange(newText.toDouble())
                }
            }
        },
        modifier = modifier,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        )
    )
}