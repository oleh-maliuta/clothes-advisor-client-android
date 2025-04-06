package com.olehmaliuta.clothesadvisor.api.http.services

import com.olehmaliuta.clothesadvisor.api.http.data.responses.MessageResponse
import retrofit2.Call
import retrofit2.http.GET

interface PingService {
    @GET("ping")
    fun ping(): Call<MessageResponse>
}