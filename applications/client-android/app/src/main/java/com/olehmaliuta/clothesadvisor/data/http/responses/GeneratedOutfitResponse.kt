package com.olehmaliuta.clothesadvisor.data.http.responses

import com.google.gson.annotations.SerializedName

data class GeneratedOutfitResponse (
    @SerializedName("items") var items: List<GeneratedItemResponse>? = null,
    @SerializedName("score_avg") var scoreAvg: Double? = null,
    @SerializedName("palette_type") var paletteType: String? = null,
)