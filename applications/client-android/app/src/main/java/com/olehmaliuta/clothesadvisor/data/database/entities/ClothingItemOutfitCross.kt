package com.olehmaliuta.clothesadvisor.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "clothing_item_outfit_cross",
    primaryKeys = ["clothing_item_id", "outfit_id"],
    foreignKeys = [
        ForeignKey(
            entity = ClothingItem::class,
            parentColumns = ["id"],
            childColumns = ["clothing_item_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Outfit::class,
            parentColumns = ["id"],
            childColumns = ["outfit_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("outfit_id")]
)
data class ClothingItemOutfitCross (
    @ColumnInfo(name = "clothing_item_id")
    val clothingItemId: Long,
    @ColumnInfo(name = "outfit_id")
    val outfitId: Long,
)