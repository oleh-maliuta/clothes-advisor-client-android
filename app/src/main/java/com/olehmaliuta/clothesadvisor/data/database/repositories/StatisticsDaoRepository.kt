package com.olehmaliuta.clothesadvisor.data.database.repositories

import com.olehmaliuta.clothesadvisor.data.database.access.StatisticsDao
import com.olehmaliuta.clothesadvisor.data.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.data.database.entities.query.ClothingItemUsage
import com.olehmaliuta.clothesadvisor.data.database.entities.query.CountPerValue
import com.olehmaliuta.clothesadvisor.data.database.entities.query.FavoriteItemStats
import kotlinx.coroutines.flow.Flow

class StatisticsDaoRepository(
    private val dao: StatisticsDao
) {
    fun totalItemsCount(): Flow<Long> {
        return dao.totalItemsCount()
    }

    fun itemsCountPerSeason(): Flow<List<CountPerValue>> {
        return dao.itemsCountPerSeason()
    }

    fun itemsCountPerCategory(): Flow<List<CountPerValue>> {
        return dao.itemsCountPerCategory()
    }

    fun oldestItems(): Flow<List<ClothingItem>> {
        return dao.oldestItems()
    }

    fun mostUsedItems(): Flow<List<ClothingItemUsage>> {
        return dao.mostUsedItems()
    }

    fun favoriteItemsPercentage(): Flow<FavoriteItemStats> {
        return dao.favoriteItemsPercentage()
    }
}