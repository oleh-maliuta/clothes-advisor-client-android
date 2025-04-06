package com.olehmaliuta.clothesadvisor.api.http.services

import com.olehmaliuta.clothesadvisor.api.http.data.bodies.UserCredentialsBody
import com.olehmaliuta.clothesadvisor.api.http.data.responses.MessageResponse
import com.olehmaliuta.clothesadvisor.api.http.data.responses.UserProfileResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface UserService {
    @POST("register")
    fun register(
        @Body body: UserCredentialsBody
    ): Call<Unit>

    @POST("token")
    fun logIn(
        @Body body: UserCredentialsBody
    ): Call<MessageResponse>

    @GET("profile")
    fun profile(
        @HeaderMap headers: Map<String, String>
    ): Call<UserProfileResponse>
}