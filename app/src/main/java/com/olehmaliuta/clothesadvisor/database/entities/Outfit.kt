package com.olehmaliuta.clothesadvisor.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outfits")
data class Outfit (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)