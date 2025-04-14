package com.olehmaliuta.clothesadvisor.database.access

import androidx.room.Dao
import androidx.room.Query
import com.olehmaliuta.clothesadvisor.database.entities.Outfit
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    @Query("SELECT * FROM outfits")
    fun getAllClothingItems(): Flow<List<Outfit>>
}