package com.olehmaliuta.clothesadvisor.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.olehmaliuta.clothesadvisor.R

@Composable
fun InfoDialog(
    title: String,
    content: String?,
    onConfirm: () -> Unit
) {
    if (content == null) {
        return
    }

    AlertDialog(
        onDismissRequest = onConfirm,
        title = { Text(title) },
        text = { Text(content.toString()) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.info_dialog__ok_button))
            }
        }
    )
}