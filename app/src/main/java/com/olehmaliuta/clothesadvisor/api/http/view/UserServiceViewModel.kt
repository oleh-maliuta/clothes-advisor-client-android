package com.olehmaliuta.clothesadvisor.api.http.view

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.olehmaliuta.clothesadvisor.api.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.api.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.api.http.security.ApiState
import com.olehmaliuta.clothesadvisor.api.http.services.UserService
import com.olehmaliuta.clothesadvisor.navigation.StateHandler
import kotlinx.coroutines.launch

class UserServiceViewModel(
    context: Context
) : ViewModel(), StateHandler {
    private val service = HttpServiceManager.buildService(UserService::class.java)
    private val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    var registrationState by mutableStateOf<ApiState<String?>>(ApiState.Idle)
        private set
    var logInState by mutableStateOf<ApiState<String?>>(ApiState.Idle)
        private set
    var forgotPasswordState by mutableStateOf<ApiState<String?>>(ApiState.Idle)
        private set
    var changeEmailState by mutableStateOf<ApiState<String?>>(ApiState.Idle)
        private set

    override fun restoreState() {
        registrationState = ApiState.Idle
        logInState = ApiState.Idle
        forgotPasswordState = ApiState.Idle
        changeEmailState = ApiState.Idle
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
        locale: String = "en"
    ) {
        viewModelScope.launch {
            logInState = ApiState.Loading

            try {
                val response = service.logIn(email, password, locale)
                val body = response.body()

                if (response.isSuccessful) {
                    logInState = ApiState.Success(body?.detail)
                    sharedPref.edit {
                        putString("token", body?.data?.accessToken)
                        putString("token_type", body?.data?.tokenType)
                    }
                    return@launch
                } else {
                    val errorBody = Gson().fromJson(
                        response.errorBody()?.string(),
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
}