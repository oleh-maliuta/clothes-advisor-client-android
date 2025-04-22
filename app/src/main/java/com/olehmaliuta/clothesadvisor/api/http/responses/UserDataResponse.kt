package com.olehmaliuta.clothesadvisor.api.http.responses

import com.google.gson.annotations.SerializedName

data class UserDataResponse (
    @SerializedName("items") var items: List<ClothingItemResponse>? = null,
    @SerializedName("combinations") var combinations: List<CombinationResponse>? = null,
)