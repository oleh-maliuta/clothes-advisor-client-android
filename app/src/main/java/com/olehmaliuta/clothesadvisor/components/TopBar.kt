package com.olehmaliuta.clothesadvisor.components

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.olehmaliuta.clothesadvisor.MainActivity
import com.olehmaliuta.clothesadvisor.api.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.api.http.data.responses.MessageResponse
import com.olehmaliuta.clothesadvisor.api.http.services.PingService
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun TopBar(activity: MainActivity, router: Router) {
    val pingService by remember {
        mutableStateOf(HttpServiceManager.buildService(PingService::class.java))
    }
    val currentBackStackEntry by router.getController().currentBackStackEntryAsState()
    var authorized by remember { mutableStateOf<Boolean>(false) }

    LaunchedEffect(currentBackStackEntry) {
        pingService.ping().enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>)
            {
                try {
                    if (response.code() == 200) {
                        Log.i("HTTP Ping Message", response.body()?.message.toString())
                    } else {
                        MessageResponse(message = "HTTP Fail")
                    }
                } catch (ex: java.lang.Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                t.message?.let { Log.e("Api failed", it) }
            }
        })
    }

    if (authorized) {
        AuthorizedTopMenu()
    } else {
        GuestTopMenu(router)
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
fun AuthorizedTopMenu() {
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
            text = "my_email_address@gmail.com",
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