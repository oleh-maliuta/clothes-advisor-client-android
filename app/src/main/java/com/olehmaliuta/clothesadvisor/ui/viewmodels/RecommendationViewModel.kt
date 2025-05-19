package com.olehmaliuta.clothesadvisor.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.olehmaliuta.clothesadvisor.navigation.StateHandler

class RecommendationViewModel() : ViewModel(), StateHandler {
    class Factory() : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecommendationViewModel::class.java)) {
                return RecommendationViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    override fun restoreState() {}
}