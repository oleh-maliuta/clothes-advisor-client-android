package com.olehmaliuta.clothesadvisor.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.olehmaliuta.clothesadvisor.api.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.api.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.api.http.security.ApiState
import com.olehmaliuta.clothesadvisor.api.http.services.UserApiService
import com.olehmaliuta.clothesadvisor.database.entities.query.OutfitWithClothingItemIds
import com.olehmaliuta.clothesadvisor.database.repositories.ClothingItemDaoRepository
import com.olehmaliuta.clothesadvisor.database.repositories.OutfitDaoRepository
import com.olehmaliuta.clothesadvisor.navigation.StateHandler
import com.olehmaliuta.clothesadvisor.tools.FileTool
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserViewModel(
    private val clothingItemDaoRepository: ClothingItemDaoRepository,
    private val outfitDaoRepository: OutfitDaoRepository,
    context: Context
) : ViewModel(), StateHandler {
    class Factory(
        private val clothingItemDaoRepository: ClothingItemDaoRepository,
        private val outfitDaoRepository: OutfitDaoRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                return UserViewModel(
                    clothingItemDaoRepository,
                    outfitDaoRepository,
                    context
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val service = HttpServiceManager.buildService(UserApiService::class.java)
    private val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    private val gson = Gson()

    var registrationState by mutableStateOf<ApiState<String?>>(ApiState.Idle)
        private set
    var logInState by mutableStateOf<ApiState<String?>>(ApiState.Idle)
        private set
    var forgotPasswordState by mutableStateOf<ApiState<String?>>(ApiState.Idle)
        private set
    var changeEmailState by mutableStateOf<ApiState<String?>>(ApiState.Idle)
        private set
    var changePasswordState by mutableStateOf<ApiState<String?>>(ApiState.Idle)
        private set

    override fun restoreState() {
        registrationState = ApiState.Idle
        logInState = ApiState.Idle
        forgotPasswordState = ApiState.Idle
        changeEmailState = ApiState.Idle
        changePasswordState = ApiState.Idle
    }

    fun register(
        email: String,
        password: String,
        locale: String = "en"
    ) {
        viewModelScope.launch {
            registrationState = ApiState.Loading

            try {
                val response = service.register(email, password, locale)

                if (response.isSuccessful) {
                    registrationState = ApiState.Success(response.body()?.detail)
                } else {
                    val errorBody = gson.fromJson(
                        response.errorBody()?.string(),
                        BaseResponse::class.java)
                    registrationState = ApiState.Error(errorBody.detail)
                }
            } catch (e: Exception) {
                registrationState = ApiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun logIn(
        email: String,
        password: String,
        syncByServerData: Boolean,
        context: Context
    ) {
        viewModelScope.launch {
            logInState = ApiState.Loading

            try {
                val logInResponse = service.logIn(email, password)

                if (logInResponse.isSuccessful) {
                    val clothingItems = if (!syncByServerData)
                        clothingItemDaoRepository.getAllClothingItems() else emptyList()
                    val outfits = if (!syncByServerData)
                        outfitDaoRepository.getOutfitsWithClothingItems().map {
                            outfitWithItems ->
                            OutfitWithClothingItemIds(
                                outfitWithItems.outfit.id,
                                outfitWithItems.outfit.name,
                                outfitWithItems.clothingItems
                                    .map { item -> item.id }
                            )
                        } else emptyList()

                    var files: List<File> = if (!syncByServerData)
                        clothingItems.map { clothingItem ->
                            val file = if (
                                clothingItem.filename.startsWith("file://") ||
                                clothingItem.filename.startsWith("content://")
                            ) {
                                val uri = clothingItem.filename.toUri()
                                FileTool.persistUriPermission(context, uri)
                                FileTool.getFileFromUri(context, uri)
                            } else {
                                FileTool.downloadFileByUrl(
                                    context,
                                    clothingItem.filename.replace(
                                        "://localhost", "://10.0.2.2"))
                            }

                            if (file != null) {
                                file
                            } else {
                                logInState = ApiState.Error(
                                    "Image were not found by the path: " +
                                            clothingItem.filename)
                                return@launch
                            }
                        } else emptyList()

                    val multipartFiles = if (!syncByServerData)
                        FileTool.filesToMultipartBodyFiles(
                            files = files,
                            partName = "files") else null

                    val logInBody = logInResponse.body()

                    val synchronizeResponse = service.synchronize(
                        token = "${logInBody?.data?.tokenType ?: "bearer"} " +
                                "${logInBody?.data?.accessToken}",
                        files = multipartFiles,
                        clothingItems = gson.toJson(clothingItems)
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        clothingCombinations = gson.toJson(outfits)
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        isServerToLocal = syncByServerData.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull())
                    )

                    if (synchronizeResponse.isSuccessful) {
                        val synchronizedBody = synchronizeResponse.body()

                        clothingItemDaoRepository.deleteAllRows()
                        outfitDaoRepository.deleteAllRows()
                        clothingItemDaoRepository.insertEntities(
                            synchronizedBody?.data?.items?.map {
                                    el -> return@map el.toClothingItemDbEntity()
                            } ?: emptyList()
                        )
                        outfitDaoRepository.insertOutfitsWithItemsByHttpResponse(
                            synchronizedBody?.data?.combinations ?:
                            emptyList()
                        )

                        sharedPref.edit {
                            putString("token", logInBody?.data?.accessToken)
                            putString("token_type", logInBody?.data?.tokenType)
                            putString("synchronized_at",
                                synchronizedBody?.synchronizedAt)
                        }

                        logInState = ApiState.Success(logInBody?.detail)
                    } else {
                        val errorBody = gson.fromJson(
                            synchronizeResponse.errorBody()?.string(),
                            BaseResponse::class.java)
                        logInState = ApiState.Error(errorBody.detail)
                    }

                    files.forEach { f -> f.delete() }
                } else {
                    val errorBody = gson.fromJson(
                        logInResponse.errorBody()?.string(),
                        BaseResponse::class.java)
                    logInState = ApiState.Error(errorBody.detail)
                }
            } catch (e: Exception) {
                logInState = ApiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun forgotPassword(
        email: String,
        locale: String = "en"
    ) {
        viewModelScope.launch {
            forgotPasswordState = ApiState.Loading

            try {
                val response = service.forgotPassword(email, locale)

                if (response.isSuccessful) {
                    forgotPasswordState = ApiState.Success(response.body()?.detail)
                    return@launch
                } else {
                    val errorBody = gson.fromJson(
                        response.errorBody()?.string(),
                        BaseResponse::class.java)
                    forgotPasswordState = ApiState.Error(errorBody.detail)
                }
            } catch (e: Exception) {
                forgotPasswordState = ApiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun changeEmail(
        newEmail: String,
        password: String,
        locale: String = "en"
    ) {
        viewModelScope.launch {
            changeEmailState = ApiState.Loading

            val token = sharedPref.getString("token", "")
            val tokenType = sharedPref.getString("token_type", null)

            try {
                val response = service.changeEmail(
                    "${tokenType ?: "bearer"} $token",
                    newEmail,
                    password,
                    locale
                )

                if (response.isSuccessful) {
                    changeEmailState = ApiState.Success(response.body()?.detail)
                    return@launch
                } else {
                    val errorBody = gson.fromJson(
                        response.errorBody()?.string(),
                        BaseResponse::class.java)
                    changeEmailState = ApiState.Error(errorBody.detail)
                }
            } catch (e: Exception) {
                changeEmailState = ApiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String
    ) {
        viewModelScope.launch {
            changePasswordState = ApiState.Loading

            val token = sharedPref.getString("token", "")
            val tokenType = sharedPref.getString("token_type", null)

            try {
                val response = service.changePassword(
                    "${tokenType ?: "bearer"} $token",
                    oldPassword,
                    newPassword
                )

                if (response.isSuccessful) {
                    changePasswordState = ApiState.Success(response.body()?.detail)
                    return@launch
                } else {
                    val errorBody = gson.fromJson(
                        response.errorBody()?.string(),
                        BaseResponse::class.java)
                    changePasswordState = ApiState.Error(errorBody.detail)
                }
            } catch (e: Exception) {
                changePasswordState = ApiState.Error("Network error: ${e.message}")
            }
        }
    }
}