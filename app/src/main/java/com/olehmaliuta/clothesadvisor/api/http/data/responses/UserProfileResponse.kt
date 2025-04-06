package com.olehmaliuta.clothesadvisor.api.http.data.responses

import com.google.gson.annotations.SerializedName

data class UserProfileResponse (
    @SerializedName("id") var id: Int? = null,
    @SerializedName("email") var email: String? = null,
)