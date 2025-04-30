package com.olehmaliuta.clothesadvisor.api.http.responses

import com.google.gson.annotations.SerializedName

data class NewCombinationResponse (
    @SerializedName("combination_id") var combinationId: Long? = null,
)