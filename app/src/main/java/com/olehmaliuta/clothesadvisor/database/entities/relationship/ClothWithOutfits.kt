package com.olehmaliuta.clothesadvisor.database.entities.relationship

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.olehmaliuta.clothesadvisor.database.entities.Cloth
import com.olehmaliuta.clothesadvisor.database.entities.ClothOutfitCross
import com.olehmaliuta.clothesadvisor.database.entities.Outfit

data class ClothWithOutfits (
    @Embedded val cloth: Cloth,
    @Relation(
        parentColumn = "cloth_id",
        entityColumn = "outfit_id",
        associateBy = Junction(ClothOutfitCross::class)
    )
    val outfits: List<Outfit>
)