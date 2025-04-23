package com.olehmaliuta.clothesadvisor.api.http.responses

import com.google.gson.annotations.SerializedName

data class ToggleFavoriteResponse (
    @SerializedName("is_favorite") var isFavorite: Boolean? = null,
)