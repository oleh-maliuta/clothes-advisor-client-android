package com.olehmaliuta.clothesadvisor.api.http.services

import com.olehmaliuta.clothesadvisor.api.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.api.http.responses.TokenResponse
import com.olehmaliuta.clothesadvisor.api.http.responses.UserDataResponse
import com.olehmaliuta.clothesadvisor.api.http.responses.UserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface UserApiService {
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
    ): Response<BaseResponse<TokenResponse>>

    @GET("profile")
    suspend fun profile(
        @Header("Authorization") token: String,
    ): Response<BaseResponse<UserProfileResponse>>

    @Multipart
    @POST("synchronize")
    suspend fun synchronize(
        @Header("Authorization") token: String,
        @Part("clothing_items") clothingItems: RequestBody,
        @Part("clothing_combinations") clothingCombinations: RequestBody,
        @Part files: List<MultipartBody.Part>?,
        @Part("is_server_to_local") isServerToLocal: RequestBody
    ): Response<BaseResponse<UserDataResponse>>

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

    @FormUrlEncoded
    @PUT("change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String,
    ): Response<BaseResponse<Nothing>>
}