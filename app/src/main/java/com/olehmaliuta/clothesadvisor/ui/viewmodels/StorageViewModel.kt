package com.olehmaliuta.clothesadvisor.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.olehmaliuta.clothesadvisor.navigation.StateHandler

class StorageViewModel() : ViewModel(), StateHandler {
    class Factory() : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StorageViewModel::class.java)) {
                return StorageViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    var initialItemIds = mutableStateOf(setOf<Long>())

    override fun restoreState() {
        initialItemIds.value = emptySet()
    }
}