package com.olehmaliuta.clothesadvisor.database.entities.query

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItemOutfitCross
import com.olehmaliuta.clothesadvisor.database.entities.Outfit

data class ClothingItemWithOutfits (
    @Embedded val clothingItem: ClothingItem,
    @Relation(
        parentColumn = "clothing_item_id",
        entityColumn = "outfit_id",
        associateBy = Junction(ClothingItemOutfitCross::class)
    )
    val outfits: List<Outfit>
)