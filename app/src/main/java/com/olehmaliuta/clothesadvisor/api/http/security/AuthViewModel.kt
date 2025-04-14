package com.olehmaliuta.clothesadvisor.api.http.security

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.olehmaliuta.clothesadvisor.api.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.api.http.services.UserApiService
import kotlinx.coroutines.launch

class AuthViewModel(
    context: Context
) : ViewModel() {
    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val service = HttpServiceManager.buildService(UserApiService::class.java)
    private val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    var authState = mutableStateOf<AuthState>(AuthState.Loading)

    fun profile() {
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
                    "${tokenType ?: "bearer"} $token")

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