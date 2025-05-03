package com.olehmaliuta.clothesadvisor.data.database.entities.query

import androidx.room.ColumnInfo

data class OutfitWithClothingItemsCount (
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "item_count") val itemCount: Long
)