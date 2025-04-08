package com.olehmaliuta.clothesadvisor.api.http.security

import com.olehmaliuta.clothesadvisor.api.http.responses.UserProfileResponse

sealed class AuthState {
    data class Authenticated(val user: UserProfileResponse?) : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}