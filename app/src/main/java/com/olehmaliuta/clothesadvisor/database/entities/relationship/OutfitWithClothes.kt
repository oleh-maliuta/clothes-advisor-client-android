package com.olehmaliuta.clothesadvisor.database.entities.relationship

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.olehmaliuta.clothesadvisor.database.entities.Cloth
import com.olehmaliuta.clothesadvisor.database.entities.ClothOutfitCross
import com.olehmaliuta.clothesadvisor.database.entities.Outfit

data class OutfitWithClothes (
    @Embedded val outfit: Outfit,
    @Relation(
        parentColumn = "outfit_id",
        entityColumn = "cloth_id",
        associateBy = Junction(ClothOutfitCross::class)
    )
    val clothes: List<Cloth>
)