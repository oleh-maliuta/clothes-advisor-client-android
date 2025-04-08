package com.olehmaliuta.clothesadvisor.api.http.services

import com.olehmaliuta.clothesadvisor.api.http.responses.TokenResponse
import com.olehmaliuta.clothesadvisor.api.http.responses.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("locale") locale: String
    ): Response<Unit>

    @FormUrlEncoded
    @POST("token")
    suspend fun logIn(
        @Field("username") email: String,
        @Field("password") password: String,
    ): Response<TokenResponse>

    @GET("profile")
    suspend fun profile(
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>
}