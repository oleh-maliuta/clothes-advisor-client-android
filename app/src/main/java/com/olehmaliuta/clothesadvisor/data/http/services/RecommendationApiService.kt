package com.olehmaliuta.clothesadvisor.data.http.services

import com.olehmaliuta.clothesadvisor.data.http.requests.RecommendationRequest
import com.olehmaliuta.clothesadvisor.data.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.data.http.responses.RecommendationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RecommendationApiService {
    @POST("recommendations")
    suspend fun recommendations(
        @Header("Authorization") token: String,
        @Body body: RecommendationRequest
    ): Response<BaseResponse<RecommendationResponse>>
}