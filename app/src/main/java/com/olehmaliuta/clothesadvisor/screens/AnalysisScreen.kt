package com.olehmaliuta.clothesadvisor.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen

@Composable
fun AnalysisScreen(
    router: Router,
    authViewModel: AuthViewModel,
) {
    when (authViewModel.authState.value) {
        is AuthState.Authenticated -> {
            ContentForUser()
        }
        AuthState.Unauthenticated -> {
            ContentForGuest(
                router = router
            )
        }
        else -> {}
    }
}

@Composable
private fun ContentForUser() {
    Text("Analysis")
}

@Composable
private fun ContentForGuest(
    router: Router
) {
    CenteredScrollContainer(
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Column {
            Text(
                text = "You need to log in to use the analysis feature.",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    lineHeight = 35.sp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { router.navigate(Screen.LogIn.name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Log In",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { router.navigate(Screen.Registration.name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Text(
                    text = "Sign Up",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}