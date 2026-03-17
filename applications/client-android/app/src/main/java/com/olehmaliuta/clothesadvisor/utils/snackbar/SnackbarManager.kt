package com.olehmaliuta.clothesadvisor.utils.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SnackbarManager private constructor() {
    companion object {
        @Volatile
        private var INSTANCE: SnackbarManager? = null

        fun getInstance(): SnackbarManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SnackbarManager().also { INSTANCE = it }
            }
        }
    }

    data class SnackbarMessage(
        val message: String,
        val actionLabel: String? = null,
        val duration: SnackbarDuration = SnackbarDuration.Short,
        val onAction: (() -> Unit)? = null
    )

    private val messages: MutableList<SnackbarMessage> = mutableListOf()
    private var snackbarHostState: SnackbarHostState? = null

    fun setSnackbarHostState(state: SnackbarHostState) {
        snackbarHostState = state
    }

    suspend fun showMessage(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        snackbarHostState?.showSnackbar(
            message = message,
            duration = duration,
            withDismissAction = true
        )
    }

    suspend fun showMessageWithAction(
        message: String,
        actionLabel: String,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onActionPerformed: () -> Unit = {}
    ) {
        snackbarHostState?.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration,
            withDismissAction = true
        )?.let {
            if (it == SnackbarResult.ActionPerformed) {
                onActionPerformed()
            }
        }
    }

    fun queueMessage(message: SnackbarMessage) {
        messages.add(message)
        processQueue()
    }

    private fun processQueue() {
        if (messages.isNotEmpty()) {
            val message = messages.first()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    if (message.actionLabel != null) {
                        showMessageWithAction(
                            message.message,
                            message.actionLabel,
                            message.duration,
                            message.onAction ?: {}
                        )
                    } else {
                        showMessage(
                            message.message,
                            message.duration
                        )
                    }

                    messages.removeAt(0)
                    processQueue()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}