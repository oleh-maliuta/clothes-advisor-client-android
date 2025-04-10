package com.olehmaliuta.clothesadvisor.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun OkDialog(
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
                Text("OK")
            }
        }
    )
}