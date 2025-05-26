package com.olehmaliuta.clothesadvisor.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
        title = {
            Text(
                text = title,
                modifier = Modifier
                    .testTag("info_dialog__title")
            )
        },
        text = {
            Text(
                text = content.toString(),
                modifier = Modifier
                    .testTag("info_dialog__description")
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .testTag("info_dialog__ok_button")
            ) {
                Text(stringResource(R.string.info_dialog__ok_button))
            }
        },
        modifier = Modifier
            .testTag("info_dialog"),
    )
}