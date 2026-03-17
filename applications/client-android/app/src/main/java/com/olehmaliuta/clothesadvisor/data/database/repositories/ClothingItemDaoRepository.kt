package com.olehmaliuta.clothesadvisor.data.database.repositories

import com.olehmaliuta.clothesadvisor.data.database.access.ClothingItemDao
import com.olehmaliuta.clothesadvisor.data.database.entities.ClothingItem
import kotlinx.coroutines.flow.Flow

class ClothingItemDaoRepository(
    private val dao: ClothingItemDao
) {
    suspend fun insertEntity(
        item: ClothingItem
    ): Long {
        return dao.insertEntity(item)
    }

    suspend fun insertEntities(
        items: List<ClothingItem>
    ) {
        dao.insertEntities(items)
    }

    suspend fun updateEntity(
        item: ClothingItem
    ) {
        dao.updateEntity(item)
    }

    suspend fun updateIsFavoriteValue(
        id: Long
    ) {
        dao.updateIsFavoriteValue(id)
    }

    suspend fun deleteItemById(
        id: Long
    ) {
        dao.deleteItemById(id)
    }

    suspend fun deleteAllRows() {
        dao.deleteAllRows()
    }

    suspend fun getAllClothingItems(): List<ClothingItem> {
        return dao.getAllClothingItems()
    }

    fun countClothingItems(): Flow<Long> {
        return dao.countClothingItems()
    }

    fun getItemById(
        id: Long?
    ): Flow<ClothingItem?> {
        return dao.getItemById(id)
    }

    fun getItemsByIds(
        ids: List<Long>
    ): Flow<List<ClothingItem>> {
        return dao.getItemsByIds(ids)
    }

    fun getUniqueCategoriesByIds(
        ids: List<Long>
    ): Flow<List<String>> {
        return dao.getUniqueCategoriesByIds(ids)
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