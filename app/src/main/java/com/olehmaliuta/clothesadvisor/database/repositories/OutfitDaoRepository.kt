package com.olehmaliuta.clothesadvisor.database.repositories

import com.olehmaliuta.clothesadvisor.api.http.responses.CombinationResponse
import com.olehmaliuta.clothesadvisor.database.access.OutfitDao
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItemOutfitCross
import com.olehmaliuta.clothesadvisor.database.entities.Outfit
import com.olehmaliuta.clothesadvisor.database.entities.query.OutfitWithClothingItemCount
import com.olehmaliuta.clothesadvisor.database.entities.query.OutfitWithClothingItemIds
import kotlinx.coroutines.flow.Flow

class OutfitDaoRepository(
    private val dao: OutfitDao
) {
    suspend fun insertOutfitsWithItems(
        outfit: Outfit,
        itemIds: List<Int>
    ) {
        dao.insertOutfitsWithItems(
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
        itemIds: List<Int>
    ) {
        dao.updateOutfitsWithItems(
            outfit,
            itemIds
        )
    }

    suspend fun deleteOutfitById(
        id: Int
    ) {
        dao.deleteOutfitById(id)
    }

    suspend fun deleteAllRows() {
        dao.deleteAllRows()
    }

    suspend fun getOutfitsWithClothingItemIds(): List<OutfitWithClothingItemIds> {
        return dao.getOutfitsWithClothingItemIds()
    }

    fun countOutfits(): Flow<Int> {
        return dao.countOutfits()
    }

    fun getOutfitWithItemIdsById(
        id: Int?
    ): Flow<OutfitWithClothingItemIds?> {
        return dao.getOutfitWithItemIdsById(id)
    }

    fun searchOutfits(
        query: String
    ): Flow<List<OutfitWithClothingItemCount>> {
        return dao.searchOutfitsWithClothingItemCount(query)
    }
}