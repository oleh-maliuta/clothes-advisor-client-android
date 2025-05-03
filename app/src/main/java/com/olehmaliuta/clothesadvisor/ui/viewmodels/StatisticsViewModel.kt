package com.olehmaliuta.clothesadvisor.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.olehmaliuta.clothesadvisor.data.database.repositories.StatisticsDaoRepository
import com.olehmaliuta.clothesadvisor.ui.navigation.StateHandler

class StatisticsViewModel(
    repository: StatisticsDaoRepository
): ViewModel(), StateHandler {
    class Factory(
        private val repository: StatisticsDaoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
                return StatisticsViewModel(
                    repository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    override fun restoreState() {}

    val totalItemsCount = repository.totalItemsCount()
    val itemsCountPerSeason = repository.itemsCountPerSeason()
    val itemsCountPerCategory = repository.itemsCountPerCategory()
    val oldestItems = repository.oldestItems()
    val mostUsedItems = repository.mostUsedItems()
    val favoriteItemsPercentage = repository.favoriteItemsPercentage()
}