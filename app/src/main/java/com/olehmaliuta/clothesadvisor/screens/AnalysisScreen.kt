package com.olehmaliuta.clothesadvisor.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.api.http.security.AuthState
import com.olehmaliuta.clothesadvisor.api.http.security.AuthViewModel
import com.olehmaliuta.clothesadvisor.components.CenteredScrollContainer

@Composable
fun AnalysisScreen(
    authViewModel: AuthViewModel,
) {
    when (authViewModel.authState.value) {
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
    Text("Analysis")
}

@Composable
private fun ContentForGuest() {
    CenteredScrollContainer(
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "You need to log in to use the analysis feature.",
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                lineHeight = 35.sp
            )
        )
    }
}