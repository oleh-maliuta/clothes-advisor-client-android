package com.olehmaliuta.clothesadvisor.database.entities.query

import androidx.room.ColumnInfo

data class OutfitWithClothingItemCount (
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "item_count") val itemCount: Long
)