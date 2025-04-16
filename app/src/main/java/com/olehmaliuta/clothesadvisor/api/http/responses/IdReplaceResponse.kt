package com.olehmaliuta.clothesadvisor.api.http.responses

import com.google.gson.annotations.SerializedName

data class IdReplaceResponse (
    @SerializedName("old") var oldId: Int? = null,
    @SerializedName("new") var newId: Int? = null,
)