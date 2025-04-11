package com.olehmaliuta.clothesadvisor.api.http.security

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olehmaliuta.clothesadvisor.api.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.api.http.services.UserService
import kotlinx.coroutines.launch

class AuthViewModel(
    context: Context
) : ViewModel() {
    private val service = HttpServiceManager.buildService(UserService::class.java)
    private val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    var authState = mutableStateOf<AuthState>(AuthState.Loading)

    fun checkAuth(
        locale: String = "en"
    ) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            try {
                val token = sharedPref.getString("token", null)
                val tokenType = sharedPref.getString("token_type", null)

                if (token == null) {
                    authState.value = AuthState.Unauthenticated
                    return@launch
                }

                val response = service.profile(
                    "${tokenType ?: "bearer"} $token",
                    locale)

                if (response.isSuccessful) {
                    authState.value = AuthState.Authenticated(response.body()?.data)
                } else if (response.code() == 401) {
                    authState.value = AuthState.Unauthenticated
                    sharedPref.edit {
                        remove("token")
                        remove("token_type")
                    }
                }
            } catch (e: Exception) {
                authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    fun logOut() {
        sharedPref.edit {
            remove("token")
            remove("token_type")
        }
        authState.value = AuthState.Unauthenticated
    }
}