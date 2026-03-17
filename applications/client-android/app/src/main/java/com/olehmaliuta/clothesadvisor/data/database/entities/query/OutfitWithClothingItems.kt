package com.olehmaliuta.clothesadvisor.data.database.entities.query

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.olehmaliuta.clothesadvisor.data.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.data.database.entities.ClothingItemOutfitCross
import com.olehmaliuta.clothesadvisor.data.database.entities.Outfit

data class OutfitWithClothingItems (
    @Embedded val outfit: Outfit,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(ClothingItemOutfitCross::class,
            parentColumn = "outfit_id",
            entityColumn = "clothing_item_id"
        )
    )
    val clothingItems: List<ClothingItem>
)