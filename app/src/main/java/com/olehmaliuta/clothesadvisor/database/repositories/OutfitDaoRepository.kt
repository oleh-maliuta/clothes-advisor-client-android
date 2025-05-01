package com.olehmaliuta.clothesadvisor.database.repositories

import com.olehmaliuta.clothesadvisor.api.http.responses.CombinationResponse
import com.olehmaliuta.clothesadvisor.database.access.OutfitDao
import com.olehmaliuta.clothesadvisor.database.entities.Outfit
import com.olehmaliuta.clothesadvisor.database.entities.query.OutfitWithClothingItemCount
import com.olehmaliuta.clothesadvisor.database.entities.query.OutfitWithClothingItems
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
    ): Flow<List<OutfitWithClothingItemCount>> {
        return dao.searchOutfitsWithClothingItemCount(query)
    }
}