package com.olehmaliuta.clothesadvisor.data.http.security

sealed class ApiState<out T> {
    object Idle : ApiState<Nothing>()
    object Loading : ApiState<Nothing>()
    data class Success<T>(val data: T) : ApiState<T>()
    data class Error(val message: String?) : ApiState<Nothing>()
}