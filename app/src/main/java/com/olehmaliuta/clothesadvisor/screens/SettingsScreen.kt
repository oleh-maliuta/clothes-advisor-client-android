package com.olehmaliuta.clothesadvisor.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.olehmaliuta.clothesadvisor.api.http.security.AuthState
import com.olehmaliuta.clothesadvisor.api.http.view.UserServiceViewModel

@Composable
fun SettingsScreen(
    userServiceViewModel: UserServiceViewModel
) {
    when (userServiceViewModel.authState) {
        is AuthState.Authenticated -> {
            ContentForUser()
        }
        AuthState.Unauthenticated -> {
            ContentForGuest()
        }
        else -> {}
    }
}

@Composable
private fun ContentForUser() {
    Text(
        text = "Settings for User"
    )
}


@Composable
private fun ContentForGuest() {
    Text(
        text = "You are not authorized!"
    )
}