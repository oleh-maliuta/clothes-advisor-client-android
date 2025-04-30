package com.olehmaliuta.clothesadvisor.database.access

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.olehmaliuta.clothesadvisor.api.http.responses.CombinationResponse
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItemOutfitCross
import com.olehmaliuta.clothesadvisor.database.entities.Outfit
import com.olehmaliuta.clothesadvisor.database.entities.query.OutfitWithClothingItemCount
import com.olehmaliuta.clothesadvisor.database.entities.query.OutfitWithClothingItemIds
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    @Transaction
    suspend fun insertOutfitsWithItems(
        outfit: Outfit,
        itemIds: List<Int>
    ) {
        insertEntity(outfit)

        itemIds.forEach { itemId ->
            val crossRef = ClothingItemOutfitCross(
                clothingItemId = itemId,
                outfitId = outfit.id
            )
            insertCrossReference(crossRef)
        }
    }

    @Transaction
    suspend fun insertOutfitsWithItemsByHttpResponse(
        combinations: List<CombinationResponse>
    ) {
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

    @Transaction
    suspend fun updateOutfitsWithItems(
        outfit: Outfit,
        itemIds: List<Int>
    ) {
        deleteCrossReferencesByOutfitId(outfit.id)
        updateEntity(outfit)

        itemIds.forEach { itemId ->
            val crossRef = ClothingItemOutfitCross(
                clothingItemId = itemId,
                outfitId = outfit.id
            )
            insertCrossReference(crossRef)
        }
    }

    @Insert
    suspend fun insertEntity(entity: Outfit)

    @Insert
    suspend fun insertCrossReference(crossRef: ClothingItemOutfitCross)

    @Update
    suspend fun updateEntity(entity: Outfit)

    @Query("DELETE FROM outfits WHERE id = :id")
    suspend fun deleteOutfitById(id: Int)

    @Query("""
        DELETE FROM clothing_item_outfit_cross
        WHERE outfit_id = :id
    """)
    suspend fun deleteCrossReferencesByOutfitId(id: Int)

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

    @Query("SELECT COUNT(*) FROM outfits")
    fun countOutfits(): Flow<Int>

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
        WHERE id = :id
        LIMIT 1
    """)
    fun getOutfitWithItemIdsById(
        id: Int?
    ): Flow<OutfitWithClothingItemIds?>

    @Query("""
        SELECT 
            o.id AS id,
            o.name AS name,
            COUNT(cioc.clothing_item_id) AS item_count
        FROM 
            outfits o
        LEFT JOIN 
            clothing_item_outfit_cross cioc ON o.id = cioc.outfit_id
        WHERE 
            o.name LIKE '%' || :query || '%' OR
            :query = ''
        GROUP BY 
            o.id, o.name
        ORDER BY 
            o.name ASC
    """)
    fun searchOutfitsWithClothingItemCount(
        query: String
    ): Flow<List<OutfitWithClothingItemCount>>
}