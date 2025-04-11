package com.olehmaliuta.clothesadvisor.api.http.services

import com.olehmaliuta.clothesadvisor.api.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.api.http.responses.TokenResponse
import com.olehmaliuta.clothesadvisor.api.http.responses.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface UserService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String,
        @Query("locale") locale: String?
    ): Response<BaseResponse<Nothing>>

    @FormUrlEncoded
    @POST("login_with_email")
    suspend fun logIn(
        @Field("email") email: String,
        @Field("password") password: String,
        @Query("locale") locale: String?
    ): Response<BaseResponse<TokenResponse>>

    @GET("profile")
    suspend fun profile(
        @Header("Authorization") token: String,
        @Query("locale") locale: String?
    ): Response<BaseResponse<UserProfileResponse>>

    @FormUrlEncoded
    @POST("forgot-password")
    suspend fun forgotPassword(
        @Field("email") email: String,
        @Query("locale") locale: String?
    ): Response<BaseResponse<Nothing>>

    @FormUrlEncoded
    @PUT("change-email")
    suspend fun changeEmail(
        @Header("Authorization") token: String,
        @Field("new_email") newEmail: String,
        @Field("password") password: String,
        @Query("locale") locale: String?
    ): Response<BaseResponse<Nothing>>
}