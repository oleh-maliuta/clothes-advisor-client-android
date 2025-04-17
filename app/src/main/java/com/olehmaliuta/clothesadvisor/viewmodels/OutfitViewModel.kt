package com.olehmaliuta.clothesadvisor.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.olehmaliuta.clothesadvisor.database.repositories.OutfitDaoRepository

class OutfitViewModel(
    val outfitDaoRepository: OutfitDaoRepository
) : ViewModel() {
    class Factory(
        private val outfitDaoRepository: OutfitDaoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OutfitViewModel::class.java)) {
                return OutfitViewModel(
                    outfitDaoRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}