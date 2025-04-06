package com.olehmaliuta.clothesadvisor.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothes")
data class Cloth(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
)