package com.olehmaliuta.clothesadvisor.database.access

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.olehmaliuta.clothesadvisor.api.http.responses.CombinationResponse
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItemOutfitCross
import com.olehmaliuta.clothesadvisor.database.entities.Outfit
import com.olehmaliuta.clothesadvisor.database.entities.query.OutfitWithClothingItemIds

@Dao
interface OutfitDao {
    @Transaction
    suspend fun insertOutfitWithItems(combinations: List<CombinationResponse>) {
        combinations.forEach { combination ->
            val outfit = Outfit(id = combination.id ?: 0, name = combination.name ?: "")
            insertEntity(outfit)

            combination.items?.forEach { itemId ->
                val crossRef = ClothingItemOutfitCross(
                    clothingItemId = itemId,
                    outfitId = outfit.id
                )
                insertCrossReference(crossRef)
            }
        }
    }

    @Insert
    suspend fun insertEntity(entity: Outfit)

    @Insert
    suspend fun insertCrossReference(crossRef: ClothingItemOutfitCross)

    @Query("DELETE FROM outfits")
    suspend fun deleteAllRows()

    @Query("""
        SELECT 
            o.id AS id,
            o.name AS name,
            (
                SELECT JSON_GROUP_ARRAY(cioc.clothing_item_id)
                FROM clothing_item_outfit_cross cioc
                WHERE cioc.outfit_id = o.id
            ) AS itemIds
        FROM outfits o
    """)
    suspend fun getOutfitsWithClothingItemIds(): List<OutfitWithClothingItemIds>
}