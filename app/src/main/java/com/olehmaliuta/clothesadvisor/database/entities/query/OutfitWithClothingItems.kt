package com.olehmaliuta.clothesadvisor.database.entities.query

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItemOutfitCross
import com.olehmaliuta.clothesadvisor.database.entities.Outfit

data class OutfitWithClothingItems (
    @Embedded val outfit: Outfit,
    @Relation(
        parentColumn = "outfit_id",
        entityColumn = "clothing_item_id",
        associateBy = Junction(ClothingItemOutfitCross::class)
    )
    val clothingItems: List<ClothingItem>
)