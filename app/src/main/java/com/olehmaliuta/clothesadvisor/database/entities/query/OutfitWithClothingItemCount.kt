package com.olehmaliuta.clothesadvisor.database.entities.query

import androidx.room.ColumnInfo

data class OutfitWithClothingItemCount (
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "item_count") val itemCount: Int
)