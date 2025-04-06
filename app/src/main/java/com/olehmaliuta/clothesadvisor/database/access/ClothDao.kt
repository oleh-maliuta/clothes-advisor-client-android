package com.olehmaliuta.clothesadvisor.database.access

import androidx.room.Dao
import androidx.room.Query
import com.olehmaliuta.clothesadvisor.database.entities.Cloth
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothDao {
    @Query("SELECT * FROM clothes")
    fun getAllClothElements(): Flow<List<Cloth>>
}