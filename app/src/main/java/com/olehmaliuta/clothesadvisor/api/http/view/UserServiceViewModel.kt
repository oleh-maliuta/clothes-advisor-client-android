package com.olehmaliuta.clothesadvisor.api.http.view

import android.content.Context
import androidx.compose.runtime.MutableState
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
import com.olehmaliuta.clothesadvisor.api.http.security.AuthState
import com.olehmaliuta.clothesadvisor.api.http.services.UserService
import com.olehmaliuta.clothesadvisor.navigation.StateHandler
import kotlinx.coroutines.launch

class UserServiceViewModel(
    context: Context,
    var authState: MutableState<AuthState>
) : ViewModel(), StateHandler {
    private val service = HttpServiceManager.buildService(UserService::class.java)
    private val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    var registrationState by mutableStateOf<ApiState<String?>>(ApiState.Idle)
        private set
    var logInState by mutableStateOf<ApiState<String?>>(ApiState.Idle)
        private set

    fun register(
        email: String,
        password: String,
        locale: String
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
        locale: String
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

    override fun restoreState() {
        registrationState = ApiState.Idle
        logInState = ApiState.Idle
    }
}