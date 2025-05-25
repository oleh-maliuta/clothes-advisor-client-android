package com.olehmaliuta.clothesadvisor.data.http.requests

import com.google.gson.annotations.SerializedName

data class RecommendationRequest (
    @SerializedName("lat") var latitude: Double,
    @SerializedName("lon") var longitude: Double,
    @SerializedName("target_time") var targetTime: String,
    @SerializedName("red") var red: Int?,
    @SerializedName("green") var green: Int?,
    @SerializedName("blue") var blue: Int?,
    @SerializedName("palette_types") var paletteTypes: List<String>?,
    @SerializedName("event") var event: String?,
    @SerializedName("include_favorites") var includeFavorites: Boolean
)