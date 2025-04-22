package com.olehmaliuta.clothesadvisor.api.http.responses

import com.google.gson.annotations.SerializedName

data class BaseResponse<T> (
    @SerializedName("data") var data: T? = null,
    @SerializedName("detail") var detail: String? = null,
    @SerializedName("synchronized_at") var synchronizedAt: String? = null,
)