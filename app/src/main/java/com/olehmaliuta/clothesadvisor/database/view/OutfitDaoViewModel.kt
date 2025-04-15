package com.olehmaliuta.clothesadvisor.database.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.olehmaliuta.clothesadvisor.database.repositories.OutfitDaoRepository

class OutfitDaoViewModel(
    val outfitDaoRepository: OutfitDaoRepository
) : ViewModel() {
    class Factory(
        private val outfitDaoRepository: OutfitDaoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OutfitDaoViewModel::class.java)) {
                return OutfitDaoViewModel(
                    outfitDaoRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}