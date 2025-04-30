package com.olehmaliuta.clothesadvisor.api.http.services

import com.olehmaliuta.clothesadvisor.api.http.requests.UploadOutfitRequest
import com.olehmaliuta.clothesadvisor.api.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.api.http.responses.NewCombinationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OutfitApiService {
    @POST("clothing-combinations")
    suspend fun addClothingCombination(
        @Header("Authorization") token: String,
        @Body body: UploadOutfitRequest
    ): Response<BaseResponse<NewCombinationResponse>>

    @PUT("clothing-combinations/{combination_id}")
    suspend fun updateClothingCombination(
        @Header("Authorization") token: String,
        @Path("combination_id") id: Long,
        @Body body: UploadOutfitRequest
    ): Response<BaseResponse<NewCombinationResponse>>

    @DELETE("clothing-combinations/{combination_id}")
    suspend fun deleteClothingCombination(
        @Header("Authorization") token: String,
        @Path("combination_id") id: Long,
    ): Response<BaseResponse<Nothing>>
}