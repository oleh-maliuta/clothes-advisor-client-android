package com.olehmaliuta.clothesadvisor.utils.snackbar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SnackbarHandler() {
    val snackbarManager = SnackbarManager.getInstance()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarManager.setSnackbarHostState(snackbarHostState)
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier,
        snackbar = { data ->
            Snackbar(
                modifier = Modifier.padding(8.dp),
                action = {
                    data.visuals.actionLabel?.let { actionLabel ->
                        TextButton(
                            onClick = { data.performAction() }
                        ) {
                            Text(
                                text = actionLabel,
                                color = MaterialTheme.colorScheme.inversePrimary
                            )
                        }
                    }
                }
            ) {
                Text(text = data.visuals.message)
            }
        }
    )
}