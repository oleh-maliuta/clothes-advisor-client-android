package com.olehmaliuta.clothesadvisor.api.http.data.bodies

data class UserCredentialsBody (
    val email: String,
    val password: String,
)