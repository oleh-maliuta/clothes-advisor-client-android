package com.olehmaliuta.clothesadvisor.database.repositories

import com.olehmaliuta.clothesadvisor.database.access.ClothingItemDao
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem

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
}