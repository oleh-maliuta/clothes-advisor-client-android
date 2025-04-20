package com.olehmaliuta.clothesadvisor.database.repositories

import com.olehmaliuta.clothesadvisor.database.access.ClothingItemDao
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem
import kotlinx.coroutines.flow.Flow

class ClothingItemDaoRepository(
    private val dao: ClothingItemDao
) {
    suspend fun insertEntity(item: ClothingItem) {
        dao.insertEntity(item)
    }

    suspend fun insertEntities(items: List<ClothingItem>) {
        dao.insertEntities(items)
    }

    suspend fun deleteAllRows() {
        dao.deleteAllRows()
    }

    suspend fun getAllClothingItems(): List<ClothingItem> {
        return dao.getAllClothingItems()
    }

    fun countClothingItems(): Flow<Int> {
        return dao.countClothingItems()
    }

    fun getItemById(id: Int?): Flow<ClothingItem?> {
        return dao.getItemById(id)
    }

    fun searchItems(
        query: String,
        sortBy: String,
        ascSort: Boolean,
        categories: List<String>,
        seasons: List<String>
    ): Flow<List<ClothingItem>> {
        return dao.searchAllFields(
            query.trim(),
            sortBy,
            ascSort,
            categories,
            seasons
        )
    }
}