package com.olehmaliuta.clothesadvisor.database.entities.query

import com.google.gson.annotations.SerializedName

data class OutfitWithClothingItemIds(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("item_ids") val itemIds: List<Long>
)