package com.olehmaliuta.clothesadvisor.api.http.services

import com.olehmaliuta.clothesadvisor.api.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.api.http.responses.ClothingItemResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ClothingItemApiService {
    @GET("clothing-items")
    suspend fun clothingItems(
        @Header("Authorization") token: String,
    ): Response<BaseResponse<ClothingItemResponse>>
}