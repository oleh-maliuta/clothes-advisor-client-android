package com.olehmaliuta.clothesadvisor.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.olehmaliuta.clothesadvisor.R

@Composable
fun AcceptCancelDialog(
    isOpen: Boolean,
    title: String,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
    acceptText: String = stringResource(R.string.accept_cancel_dialog__accept_button),
    cancelText: String = stringResource(R.string.accept_cancel_dialog__cancel_button),
    acceptEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    if (!isOpen) {
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                modifier = Modifier
                    .testTag("accept_cancel_dialog__title")
            )
        },
        text = { content() },
        confirmButton = {
            Button(
                onClick = onAccept,
                enabled = acceptEnabled,
                modifier = Modifier
                    .testTag("accept_cancel_dialog__confirm_button")
            ) {
                Text(acceptText)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Text(cancelText)
            }
        },
        modifier = Modifier
            .testTag("accept_cancel_dialog")
    )
}