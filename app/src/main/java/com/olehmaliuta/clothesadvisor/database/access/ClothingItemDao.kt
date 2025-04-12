package com.olehmaliuta.clothesadvisor.database.access

import androidx.room.Dao
import androidx.room.Query
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingItemDao {
    @Query("SELECT * FROM clothing_items")
    fun getAllClothingItems(): Flow<List<ClothingItem>>
}