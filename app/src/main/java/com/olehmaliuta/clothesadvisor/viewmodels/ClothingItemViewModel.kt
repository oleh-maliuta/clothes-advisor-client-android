package com.olehmaliuta.clothesadvisor.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.database.repositories.ClothingItemDaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClothingItemViewModel(
    val clothingItemDaoRepository: ClothingItemDaoRepository
) : ViewModel() {
    class Factory(
        private val clothingItemDaoRepository: ClothingItemDaoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClothingItemViewModel::class.java)) {
                return ClothingItemViewModel(
                    clothingItemDaoRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    init {
        viewModelScope.launch {
            clothingItemDaoRepository.getAllClothingItems().collect { newItems ->
                _allClothingItemsState.value = newItems
            }
        }
    }

    private val _allClothingItemsState =
        MutableStateFlow<List<ClothingItem>>(emptyList())

    val allClothingItems: StateFlow<List<ClothingItem>> =
        _allClothingItemsState.asStateFlow()
}