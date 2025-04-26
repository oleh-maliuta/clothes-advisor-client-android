package com.olehmaliuta.clothesadvisor.database.repositories

import com.olehmaliuta.clothesadvisor.api.http.responses.CombinationResponse
import com.olehmaliuta.clothesadvisor.database.access.OutfitDao
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItemOutfitCross
import com.olehmaliuta.clothesadvisor.database.entities.Outfit
import com.olehmaliuta.clothesadvisor.database.entities.query.OutfitWithClothingItemCount
import com.olehmaliuta.clothesadvisor.database.entities.query.OutfitWithClothingItemIds
import com.olehmaliuta.clothesadvisor.database.entities.query.OutfitWithClothingItems
import kotlinx.coroutines.flow.Flow

class OutfitDaoRepository(
    private val dao: OutfitDao
) {
    suspend fun insertOutfitWithItems(
        combination: List<CombinationResponse>
    ) {
        dao.insertOutfitWithItems(combination)
    }

    suspend fun insertOutfit(outfit: Outfit) {
        dao.insertEntity(outfit)
    }

    suspend fun insertCrossReference(crossRef: ClothingItemOutfitCross) {
        dao.insertCrossReference(crossRef)
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

    fun getOutfitWithItemsById(
        id: Int?
    ): Flow<OutfitWithClothingItems?> {
        return dao.getOutfitWithItemsById(id)
    }

    fun searchOutfits(
        query: String
    ): Flow<List<OutfitWithClothingItemCount>> {
        return dao.searchOutfitsWithClothingItemCount(query)
    }
}