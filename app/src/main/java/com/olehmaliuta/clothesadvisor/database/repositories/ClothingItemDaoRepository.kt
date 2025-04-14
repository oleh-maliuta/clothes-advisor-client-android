package com.olehmaliuta.clothesadvisor.database.repositories

import com.olehmaliuta.clothesadvisor.database.access.ClothingItemDao
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem

class ClothingItemDaoRepository(
    private val dao: ClothingItemDao
) {
    suspend fun insertItem(item: ClothingItem) {
        dao.insertItem(item)
    }

    suspend fun insertItems(items: List<ClothingItem>) {
        dao.insertItems(items)
    }

    suspend fun getAllClothingItems(): List<ClothingItem> {
        return dao.getAllClothingItems()
    }
}