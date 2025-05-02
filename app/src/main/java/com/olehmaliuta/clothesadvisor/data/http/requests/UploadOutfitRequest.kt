package com.olehmaliuta.clothesadvisor.data.http.requests

import com.google.gson.annotations.SerializedName

data class UploadOutfitRequest (
    @SerializedName("name") var name: String,
    @SerializedName("item_ids") var itemIds: List<Long>
)