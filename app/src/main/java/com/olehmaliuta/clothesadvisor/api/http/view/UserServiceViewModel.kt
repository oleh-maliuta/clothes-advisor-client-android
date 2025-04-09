package com.olehmaliuta.clothesadvisor.api.http.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olehmaliuta.clothesadvisor.api.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.api.http.security.AuthState
import com.olehmaliuta.clothesadvisor.api.http.services.UserService
import kotlinx.coroutines.launch
import androidx.core.content.edit

class UserServiceViewModel(context: Context) : ViewModel() {
    private val service = HttpServiceManager.buildService(UserService::class.java)
    private val sharedPref = context.getSharedPreferences(
        "general",
        Context.MODE_PRIVATE)
    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    fun register(
        email: String,
        password: String,
        locale: String,
        dialogState: MutableState<String?>,
        successState: MutableState<Boolean>
    ) {
        viewModelScope.launch {
            try {
                val response = service.register(email, password, locale)
                val body = response.body()

                if (response.isSuccessful) {
                    dialogState.value = body?.detail
                    successState.value = true
                }

                dialogState.value = body?.detail
            } catch (e: Exception) {
                dialogState.value = "Network error: ${e.message}"
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun logIn(
        email: String,
        password: String,
        locale: String,
        dialogState: MutableState<String?>,
        redirectRequired: MutableState<Boolean>
    ) {
        viewModelScope.launch {
            try {
                val response = service.logIn(email, password, locale)
                val body = response.body()

                if (response.isSuccessful) {
                    sharedPref.edit {
                        putString("token", body?.data?.accessToken)
                        putString("token_type", body?.data?.tokenType)
                    }
                    redirectRequired.value = true
                    return@launch
                }

                dialogState.value = body?.detail
            } catch (e: Exception) {
                dialogState.value = "Network error: ${e.message}"
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun profile(
        locale: String
    ) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val token = sharedPref.getString("token", null)
                val tokenType = sharedPref.getString("token_type", null)

                if (token == null) {
                    authState = AuthState.Unauthenticated
                    return@launch
                }

                val response = service.profile(
                    "${tokenType ?: "bearer"} $token",
                    locale)

                if (response.isSuccessful) {
                    authState = AuthState.Authenticated(response.body()?.data)
                } else if (response.code() == 401) {
                    authState = AuthState.Unauthenticated
                    sharedPref.edit {
                        remove("token")
                        remove("token_type")
                    }
                }
            } catch (e: Exception) {
                authState = AuthState.Error("Network error: ${e.message}")
            }
        }
    }
}