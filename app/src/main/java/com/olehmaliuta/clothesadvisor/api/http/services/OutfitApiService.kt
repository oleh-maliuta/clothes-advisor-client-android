package com.olehmaliuta.clothesadvisor.api.http.services

import com.olehmaliuta.clothesadvisor.api.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.api.http.responses.NewCombinationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OutfitApiService {
    @POST("clothing-combinations")
    suspend fun addClothingCombination(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Response<BaseResponse<NewCombinationResponse>>
}