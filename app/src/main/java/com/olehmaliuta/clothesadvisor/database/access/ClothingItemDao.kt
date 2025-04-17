package com.olehmaliuta.clothesadvisor.database.access

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingItemDao {
    @Insert
    suspend fun insertEntity(entity: ClothingItem)

    @Insert
    suspend fun insertEntities(entities: List<ClothingItem>)

    @Query("DELETE FROM clothing_items")
    suspend fun deleteAllRows()

    @Query("SELECT * FROM clothing_items")
    fun getAllClothingItems(): Flow<List<ClothingItem>>
}