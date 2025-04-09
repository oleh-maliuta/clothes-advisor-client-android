package com.olehmaliuta.clothesadvisor.api.http.responses

import com.google.gson.annotations.SerializedName

data class BaseResponse<T> (
    @SerializedName("data") val data: T? = null,
    @SerializedName("detail") val detail: String? = null,
)