package com.olehmaliuta.clothesadvisor.api.http.view

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.olehmaliuta.clothesadvisor.api.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.api.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.api.http.security.ApiState
import com.olehmaliuta.clothesadvisor.api.http.services.UserApiService
import com.olehmaliuta.clothesadvisor.database.repositories.ClothingItemDaoRepository
import com.olehmaliuta.clothesadvisor.navigation.StateHandler
import kotlinx.coroutines.launch

class UserApiViewModel(
    private val clothingItemDaoRepository: ClothingItemDaoRepository,
    context: Context
) : ViewModel(), StateHandler {
    class Factory(
        private val clothingItemDaoRepository: ClothingItemDaoRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserApiViewModel::class.java)) {
                return UserApiViewModel(
                    clothingItemDaoRepository,
                    context
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val service = HttpServiceManager.buildService(UserApiService::class.java)
    private val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)

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
                    val errorBody = Gson().fromJson(
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
        syncByServerData: Boolean
    ) {
        viewModelScope.launch {
            logInState = ApiState.Loading

            try {
                val logInResponse = service.logIn(email, password)

                if (logInResponse.isSuccessful) {
                    val clothingItems = if (syncByServerData)
                        clothingItemDaoRepository.getAllClothingItems() else emptyList()

                    val logInBody = logInResponse.body()

                    val synchronizeResponse = service.synchronize(
                        token = "${logInBody?.data?.tokenType ?: "bearer"} " +
                                "${logInBody?.data?.accessToken}",
                        clothingItems = clothingItems.toString(),
                        clothingCombinations = "",
                        files = null,
                        isServerToLocal = syncByServerData
                    )

                    if (synchronizeResponse.isSuccessful) {
                        val synchronizedBody = synchronizeResponse.body()

                        sharedPref.edit {
                            putString("token", logInBody?.data?.accessToken)
                            putString("token_type", logInBody?.data?.tokenType)
                            putString("synchronized_at",
                                synchronizedBody?.data?.synchronizedAt)
                        }

                        if (syncByServerData) {
                            clothingItemDaoRepository.insertItems(
                                synchronizedBody?.data?.items?.map {
                                    el -> return@map el.toClothingItemDbEntity()
                                } ?: emptyList()
                            )

                            TODO("Put all data to the room db")
                        }

                        logInState = ApiState.Success(logInBody?.detail)
                        return@launch
                    } else {
                        val errorBody = Gson().fromJson(
                            synchronizeResponse.errorBody()?.string(),
                            BaseResponse::class.java)
                        logInState = ApiState.Error(errorBody.detail)
                    }
                } else {
                    val errorBody = Gson().fromJson(
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
                    val errorBody = Gson().fromJson(
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
                    val errorBody = Gson().fromJson(
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
                    val errorBody = Gson().fromJson(
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