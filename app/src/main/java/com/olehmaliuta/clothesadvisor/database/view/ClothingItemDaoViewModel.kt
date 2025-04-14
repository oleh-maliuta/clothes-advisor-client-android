package com.olehmaliuta.clothesadvisor.database.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.olehmaliuta.clothesadvisor.database.repositories.ClothingItemDaoRepository

class ClothingItemDaoViewModel(
    val clothingItemDaoRepository: ClothingItemDaoRepository
) : ViewModel() {
    class Factory(
        private val clothingItemDaoRepository: ClothingItemDaoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClothingItemDaoViewModel::class.java)) {
                return ClothingItemDaoViewModel(
                    clothingItemDaoRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}