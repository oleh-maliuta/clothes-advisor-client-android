package com.olehmaliuta.clothesadvisor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.api.http.responses.UserProfileResponse
import com.olehmaliuta.clothesadvisor.api.http.security.AuthState
import com.olehmaliuta.clothesadvisor.api.http.view.UserServiceViewModel
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen

@Composable
fun TopBar(
    router: Router,
    userServiceViewModel: UserServiceViewModel
) {
    when (val authState = userServiceViewModel.authState) {
        is AuthState.Authenticated -> {
            AuthorizedTopMenu(authState.user ?: UserProfileResponse())
        }
        AuthState.Unauthenticated -> {
            GuestTopMenu(router)
        }
        else -> {}
    }
}

@Composable
fun GuestTopMenu(router: Router) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 8.dp)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { router.navigateTo(Screen.LogIn) },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .width(75.dp)
                    .height(32.dp)
            ) {
                Text(
                    text = "Log in",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(0.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(
                onClick = { router.navigateTo(Screen.Registration) },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .width(75.dp)
                    .height(32.dp)
            ) {
                Text(
                    text = "Sign up",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(0.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 16.sp)
                )
            }
        }
    }
}

@Composable
fun AuthorizedTopMenu(userInfo: UserProfileResponse) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 8.dp)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = userInfo.email.toString(),
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 16.sp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(end = 3.dp))

        TextButton(
            onClick = { /* ... */ },
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .width(75.dp)
                .height(32.dp)
        ) {
            Text(
                text = "Log out",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(0.dp),
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 16.sp
                )
            )
        }
    }
}