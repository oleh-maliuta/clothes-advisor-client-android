package com.olehmaliuta.clothesadvisor.data.http.responses

import com.google.gson.annotations.SerializedName

data class TokenResponse (
    @SerializedName("access_token") var accessToken: String? = null,
    @SerializedName("token_type") var tokenType: String? = null,
)