package com.olehmaliuta.clothesadvisor.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "cloth_outfit_cross",
    primaryKeys = ["cloth_id", "outfit_id"],
    foreignKeys = [
        ForeignKey(
            entity = Cloth::class,
            parentColumns = ["id"],
            childColumns = ["cloth_id"],
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
data class ClothOutfitCross (
    @ColumnInfo(name = "cloth_id")
    val clothId: Int,
    @ColumnInfo(name = "outfit_id")
    val outfitId: Int,
)