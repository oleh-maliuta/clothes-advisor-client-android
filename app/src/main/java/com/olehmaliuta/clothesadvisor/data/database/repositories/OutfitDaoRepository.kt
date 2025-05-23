package com.olehmaliuta.clothesadvisor.data.database.repositories

import com.olehmaliuta.clothesadvisor.data.http.responses.CombinationResponse
import com.olehmaliuta.clothesadvisor.data.database.access.OutfitDao
import com.olehmaliuta.clothesadvisor.data.database.entities.Outfit
import com.olehmaliuta.clothesadvisor.data.database.entities.query.OutfitWithClothingItemsCount
import com.olehmaliuta.clothesadvisor.data.database.entities.query.OutfitWithClothingItems
import kotlinx.coroutines.flow.Flow

class OutfitDaoRepository(
    private val dao: OutfitDao
) {
    suspend fun insertOutfitWithItems(
        outfit: Outfit,
        itemIds: List<Long>
    ): Long {
        return dao.insertOutfitWithItems(
            outfit,
            itemIds
        )
    }

    suspend fun insertOutfitsWithItemsByHttpResponse(
        combination: List<CombinationResponse>
    ) {
        dao.insertOutfitsWithItemsByHttpResponse(combination)
    }

    suspend fun updateOutfitsWithItems(
        outfit: Outfit,
        itemIds: List<Long>
    ) {
        dao.updateOutfitsWithItems(
            outfit,
            itemIds
        )
    }

    suspend fun deleteOutfitById(
        id: Long
    ) {
        dao.deleteOutfitById(id)
    }

    suspend fun deleteAllRows() {
        dao.deleteAllRows()
    }

    suspend fun getOutfitsWithClothingItems(): List<OutfitWithClothingItems> {
        return dao.getOutfitsWithClothingItems()
    }

    fun getOutfitWithItemsById(
        id: Long?
    ): Flow<OutfitWithClothingItems?> {
        return dao.getOutfitWithItemsById(id)
    }

    fun countOutfits(): Flow<Long> {
        return dao.countOutfits()
    }

    fun searchOutfits(
        query: String
    ): Flow<List<OutfitWithClothingItemsCount>> {
        return dao.searchOutfitsWithClothingItemCount(query)
    }
}