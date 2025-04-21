package com.olehmaliuta.clothesadvisor.api.http.responses

import com.google.gson.annotations.SerializedName

data class NewClothingItemFileResponse (
    @SerializedName("old") var oldId: Int? = null,
    @SerializedName("new") var newId: Int? = null,
    @SerializedName("new_file") var newFile: String? = null,
)