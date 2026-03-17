package com.olehmaliuta.clothesadvisor.data.database.access

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.olehmaliuta.clothesadvisor.data.http.responses.CombinationResponse
import com.olehmaliuta.clothesadvisor.data.database.entities.ClothingItemOutfitCross
import com.olehmaliuta.clothesadvisor.data.database.entities.Outfit
import com.olehmaliuta.clothesadvisor.data.database.entities.query.OutfitWithClothingItemsCount
import com.olehmaliuta.clothesadvisor.data.database.entities.query.OutfitWithClothingItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    @Transaction
    suspend fun insertOutfitWithItems(
        outfit: Outfit,
        itemIds: List<Long>
    ): Long {
        val outfitId = insertEntity(outfit)

        itemIds.forEach { itemId ->
            val crossRef = ClothingItemOutfitCross(
                clothingItemId = itemId,
                outfitId = outfitId
            )
            insertCrossReference(crossRef)
        }

        return outfitId
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
        itemIds: List<Long>
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
    suspend fun insertEntity(entity: Outfit): Long

    @Insert
    suspend fun insertCrossReference(crossRef: ClothingItemOutfitCross)

    @Update
    suspend fun updateEntity(entity: Outfit)

    @Query("DELETE FROM outfits WHERE id = :id")
    suspend fun deleteOutfitById(id: Long)

    @Query("""
        DELETE FROM clothing_item_outfit_cross
        WHERE outfit_id = :id
    """)
    suspend fun deleteCrossReferencesByOutfitId(id: Long)

    @Query("DELETE FROM outfits")
    suspend fun deleteAllRows()

    @Query("SELECT * FROM outfits")
    suspend fun getOutfitsWithClothingItems(): List<OutfitWithClothingItems>

    @Query("SELECT * FROM outfits WHERE id = :id LIMIT 1")
    fun getOutfitWithItemsById(
        id: Long?
    ): Flow<OutfitWithClothingItems?>

    @Query("SELECT COUNT(*) FROM outfits")
    fun countOutfits(): Flow<Long>

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
            LOWER(o.name) LIKE '%' || LOWER(:query) || '%' OR
            :query = ''
        GROUP BY 
            o.id, o.name
        ORDER BY 
            o.name ASC
    """)
    fun searchOutfitsWithClothingItemCount(
        query: String
    ): Flow<List<OutfitWithClothingItemsCount>>
}