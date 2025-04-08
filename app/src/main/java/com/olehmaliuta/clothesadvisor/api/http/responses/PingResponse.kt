package com.olehmaliuta.clothesadvisor.api.http.responses

import com.google.gson.annotations.SerializedName

data class PingResponse (
    @SerializedName("ping") var ping: String? = null,
)