package com.olehmaliuta.clothesadvisor.data.database.access

import androidx.room.Dao
import androidx.room.Query
import com.olehmaliuta.clothesadvisor.data.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.data.database.entities.query.ClothingItemUsage
import com.olehmaliuta.clothesadvisor.data.database.entities.query.CountPerValue
import com.olehmaliuta.clothesadvisor.data.database.entities.query.FavoriteItemStats
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {
    @Query("SELECT COUNT(*) AS total_items FROM clothing_items")
    fun totalItemsCount(): Flow<Long>

    @Query("""
        SELECT season AS value, COUNT(*) AS count
        FROM clothing_items 
        GROUP BY value
        ORDER BY count DESC
    """)
    fun itemsCountPerSeason(): Flow<List<CountPerValue>>

    @Query("""
        SELECT category AS value, COUNT(*) AS count 
        FROM clothing_items 
        GROUP BY value 
        ORDER BY count DESC
    """)
    fun itemsCountPerCategory(): Flow<List<CountPerValue>>

    @Query("""
        SELECT * FROM clothing_items 
        WHERE purchase_date IS NOT NULL
        ORDER BY purchase_date ASC 
        LIMIT 5;
    """)
    fun oldestItems(): Flow<List<ClothingItem>>

    @Query("""
        SELECT ci.id, ci.name, COUNT(cioc.outfit_id) AS usage_count
        FROM clothing_items ci
        JOIN clothing_item_outfit_cross cioc ON ci.id = cioc.clothing_item_id
        GROUP BY ci.id
        ORDER BY usage_count DESC
        LIMIT 10;
    """)
    fun mostUsedItems(): Flow<List<ClothingItemUsage>>

    @Query("""
        SELECT 
            (COUNT(CASE WHEN is_favorite = 1 THEN 1 END) * 100.0 / COUNT(*)) 
                AS favoritePercentage,
            (COUNT(CASE WHEN is_favorite = 0 THEN 1 END) * 100.0 / COUNT(*)) 
                AS nonFavoritePercentage
        FROM clothing_items
    """)
    fun favoriteItemsPercentage(): Flow<FavoriteItemStats>
}