package com.olehmaliuta.clothesadvisor.data.http.responses

import com.google.gson.annotations.SerializedName
import com.olehmaliuta.clothesadvisor.data.database.entities.ClothingItem

data class ClothingItemResponse (
    @SerializedName("id") var id: Long? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("category") var category: String? = null,
    @SerializedName("season") var season: String? = null,
    @SerializedName("red") var red: Int? = null,
    @SerializedName("green") var green: Int? = null,
    @SerializedName("blue") var blue: Int? = null,
    @SerializedName("material") var material: String? = null,
    @SerializedName("brand") var brand: String? = null,
    @SerializedName("purchase_date") var purchaseDate: String? = null,
    @SerializedName("price") var price: Double? = null,
    @SerializedName("is_favorite") var isFavorite: Boolean? = null,
    @SerializedName("filename") var filename: String? = null
) {
    fun toClothingItemDbEntity(): ClothingItem {
        return ClothingItem(
            id = id ?: 0,
            filename = filename ?: "",
            name = name ?: "",
            category = category ?: "",
            season = season ?: "",
            red = red ?: 0,
            green = green ?: 0,
            blue = blue ?: 0,
            material = material ?: "",
            brand = brand,
            purchaseDate = purchaseDate,
            price = price,
            isFavorite = isFavorite == true,
        )
    }
}