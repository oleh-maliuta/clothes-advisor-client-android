package com.olehmaliuta.clothesadvisor.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AcceptCancelDialog(
    isOpen: Boolean,
    title: String,
    onDismissRequest: () -> Unit,
    onAccept: () -> Unit,
    acceptText: String = "Accept",
    cancelText: String = "Cancel",
    acceptEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    if (!isOpen) {
        return
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = { content() },
        confirmButton = {
            Button(
                onClick = onAccept,
                enabled = acceptEnabled
            ) {
                Text(acceptText)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Text(cancelText)
            }
        }
    )
}