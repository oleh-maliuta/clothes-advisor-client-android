package com.olehmaliuta.clothesadvisor.data.http.responses

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("temp") var temp: Double? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("icon") var icon: String? = null,
)