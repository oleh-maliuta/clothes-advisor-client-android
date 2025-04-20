package com.olehmaliuta.clothesadvisor.api.http.services

import com.olehmaliuta.clothesadvisor.api.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.api.http.responses.ClothingItemResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ClothingItemApiService {
    @Multipart
    @POST("add-clothing-item")
    suspend fun addClothingItem(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("name") name: String,
        @Part("category") category: String,
        @Part("season") season: String,
        @Part("red") red: Int,
        @Part("green") green: Int,
        @Part("blue") blue: Int,
        @Part("material") material: String,
        @Part("brand") brand: String? = null,
        @Part("purchase_date") purchaseDate: String? = null,
        @Part("price") price: Double? = null,
        @Part("is_favorite") isFavorite: Boolean? = null,
    ): Response<BaseResponse<ClothingItemResponse>>
}