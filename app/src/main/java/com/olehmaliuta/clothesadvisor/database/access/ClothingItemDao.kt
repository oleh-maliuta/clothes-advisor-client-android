package com.olehmaliuta.clothesadvisor.database.access

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem

@Dao
interface ClothingItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ClothingItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ClothingItem>)

    @Query("SELECT * FROM clothing_items")
    suspend fun getAllClothingItems(): List<ClothingItem>
}