package com.olehmaliuta.clothesadvisor.data.database.entities.query

import androidx.room.ColumnInfo

data class ClothingItemUsage(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "usage_count")
    val usageCount: Long
)