package com.olehmaliuta.clothesadvisor.data.http.responses

import com.google.gson.annotations.SerializedName

data class RecommendationResponse(
    @SerializedName("weather") var weather: WeatherResponse? = null,
    @SerializedName("outfits") var outfits: List<GeneratedOutfitResponse>? = null,
)