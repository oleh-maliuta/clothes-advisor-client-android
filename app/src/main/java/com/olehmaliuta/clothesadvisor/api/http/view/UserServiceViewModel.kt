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

class UserServiceViewModel(context: Context) : ViewModel() {
    private val service = HttpServiceManager.buildService(UserService::class.java)
    private val sharedPref = context.getSharedPreferences(
        "general",
        Context.MODE_PRIVATE)
    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    @SuppressLint("CommitPrefEdits")
    fun register(
        email: String,
        password: String,
        dialogState: MutableState<String?>
    ) {
        viewModelScope.launch {
            try {
                val response = service.register(email, password, "ua")
                if (response.isSuccessful) {
                    dialogState.value = "Account was created! " +
                            "Confirm the email address in the sent mail."
                } else {
                    dialogState.value = "ERROR"
                }
            } catch (e: Exception) {
                dialogState.value = "Network error: ${e.message}"
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun profile() {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val token = sharedPref.getString("token", null)
                if (token == null) {
                    authState = AuthState.Unauthenticated
                    return@launch
                }

                val response = service.profile("Bearer $token")
                if (response.isSuccessful) {
                    authState = AuthState.Authenticated(response.body())
                } else if (response.code() == 401) {
                    authState = AuthState.Unauthenticated
                    sharedPref.edit().remove("token")
                }
            } catch (e: Exception) {
                authState = AuthState.Error("Network error: ${e.message}")
            }
        }
    }
}