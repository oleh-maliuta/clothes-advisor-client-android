package com.olehmaliuta.clothesadvisor.api.http.services

import com.olehmaliuta.clothesadvisor.api.http.responses.PingResponse
import retrofit2.Response
import retrofit2.http.GET

interface PingService {
    @GET("ping")
    suspend fun ping(): Response<PingResponse>
}