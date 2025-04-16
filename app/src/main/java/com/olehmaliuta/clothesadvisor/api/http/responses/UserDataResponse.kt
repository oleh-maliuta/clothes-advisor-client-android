package com.olehmaliuta.clothesadvisor.api.http.responses

import com.google.gson.annotations.SerializedName

data class UserDataResponse (
    @SerializedName("items") var items: List<ClothingItemResponse>? = null,
    @SerializedName("combinations") var combinations: List<CombinationResponse>? = null,
    @SerializedName("item_id_mapping") var itemIdMapping: List<IdReplaceResponse>? = null,
    @SerializedName("combo_id_mapping") var comboIdMapping: List<IdReplaceResponse>? = null,
    @SerializedName("synchronized_at") var synchronizedAt: String? = null,
)