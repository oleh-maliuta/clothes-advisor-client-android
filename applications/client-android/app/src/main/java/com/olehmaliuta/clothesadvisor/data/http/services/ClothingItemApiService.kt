package com.olehmaliuta.clothesadvisor.data.http.services

import com.olehmaliuta.clothesadvisor.data.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.data.http.responses.ClothingItemResponse
import com.olehmaliuta.clothesadvisor.data.http.responses.ToggleFavoriteResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ClothingItemApiService {
    @Multipart
    @POST("add-clothing-item")
    suspend fun addClothingItem(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("category") category: RequestBody,
        @Part("season") season: RequestBody,
        @Part("red") red: RequestBody,
        @Part("green") green: RequestBody,
        @Part("blue") blue: RequestBody,
        @Part("material") material: RequestBody,
        @Part("brand") brand: RequestBody?,
        @Part("purchase_date") purchaseDate: RequestBody?,
        @Part("price") price: RequestBody?,
        @Part("is_favorite") isFavorite: RequestBody,
    ): Response<BaseResponse<ClothingItemResponse>>

    @Multipart
    @PUT("clothing-items/{item_id}")
    suspend fun updateClothingItem(
        @Header("Authorization") token: String,
        @Path("item_id") id: Long,
        @Part file: MultipartBody.Part?,
        @Part("name") name: RequestBody,
        @Part("category") category: RequestBody,
        @Part("season") season: RequestBody,
        @Part("red") red: RequestBody,
        @Part("green") green: RequestBody,
        @Part("blue") blue: RequestBody,
        @Part("material") material: RequestBody,
        @Part("brand") brand: RequestBody?,
        @Part("purchase_date") purchaseDate: RequestBody?,
        @Part("price") price: RequestBody?,
        @Part("is_favorite") isFavorite: RequestBody,
    ): Response<BaseResponse<ClothingItemResponse>>

    @PUT("items/{item_id}/toggle-favorite")
    suspend fun toggleFavorite(
        @Header("Authorization") token: String,
        @Path("item_id") id: Long,
    ): Response<BaseResponse<ToggleFavoriteResponse>>

    @DELETE("clothing-items/{item_id}")
    suspend fun deleteClothingItem(
        @Header("Authorization") token: String,
        @Path("item_id") id: Long,
    ): Response<BaseResponse<Nothing>>
}