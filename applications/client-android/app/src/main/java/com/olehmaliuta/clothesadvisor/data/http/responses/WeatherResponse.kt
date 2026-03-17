package com.olehmaliuta.clothesadvisor.data.http.responses

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("temp") var temp: Double? = null,
    @SerializedName("code") var code: Int? = null,
    @SerializedName("icon") var icon: String? = null,
)