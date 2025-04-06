package com.olehmaliuta.clothesadvisor.api.http.data.responses

import com.google.gson.annotations.SerializedName

data class MessageResponse (
    @SerializedName("message") var message: String? = null,
)