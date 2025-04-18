package com.olehmaliuta.clothesadvisor.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.database.repositories.ClothingItemDaoRepository
import kotlinx.coroutines.flow.Flow

class ClothingItemViewModel(
    val repository: ClothingItemDaoRepository
) : ViewModel() {
    class Factory(
        private val repository: ClothingItemDaoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClothingItemViewModel::class.java)) {
                return ClothingItemViewModel(
                    repository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val countClothingItems = repository.countClothingItems()

    fun searchItems(
        query: String,
        sortBy: String,
        ascSort: Boolean,
        categories: List<String>,
        seasons: List<String>
    ): Flow<List<ClothingItem>> {
        return repository.searchItems(
            query,
            sortBy,
            ascSort,
            categories,
            seasons
        )
    }
}