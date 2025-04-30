package com.olehmaliuta.clothesadvisor.api.http.responses

import com.google.gson.annotations.SerializedName

data class UserProfileResponse (
    @SerializedName("id") var id: Long? = null,
    @SerializedName("email") var email: String? = null,
)