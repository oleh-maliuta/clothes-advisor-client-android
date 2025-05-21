package com.olehmaliuta.clothesadvisor.data.http.responses

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("temp") var temp: Double? = null,
    @SerializedName("weather") var weather: String? = null,
)