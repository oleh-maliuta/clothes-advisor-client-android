package com.olehmaliuta.clothesadvisor.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.api.http.security.ApiState
import com.olehmaliuta.clothesadvisor.api.http.security.AuthState
import com.olehmaliuta.clothesadvisor.api.http.security.AuthViewModel
import com.olehmaliuta.clothesadvisor.api.http.view.UserServiceViewModel
import com.olehmaliuta.clothesadvisor.components.CenteredScrollContainer
import com.olehmaliuta.clothesadvisor.components.OkDialog
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen

@Composable
fun SettingsScreen(
    router: Router,
    authViewModel: AuthViewModel,
    userServiceViewModel: UserServiceViewModel
) {
    when (authViewModel.authState.value) {
        is AuthState.Authenticated -> {
            ContentForUser(
                router = router,
                authViewModel = authViewModel,
                userServiceViewModel = userServiceViewModel
            )
        }
        AuthState.Unauthenticated -> {
            ContentForGuest()
        }
        else -> {}
    }
}

@Composable
private fun ContentForUser(
    router: Router,
    authViewModel: AuthViewModel,
    userServiceViewModel: UserServiceViewModel
) {
    var okDialogTitle = remember { mutableStateOf("") }
    var okDialogMessage = remember { mutableStateOf<String?>(null) }

    OkDialog(
        title = okDialogTitle.value,
        content = okDialogMessage.value,
        onConfirm = {
            okDialogMessage.value = null

            if (userServiceViewModel.changeEmailState is ApiState.Success) {
                authViewModel.logOut()
                router.navigate(
                    route = Screen.LogIn.name,
                    apiStatesToRestore = listOf(userServiceViewModel)
                )
            }
        }
    )

    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "Settings",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(),
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                lineHeight = 35.sp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        ChangeEmailForm(
            userServiceViewModel = userServiceViewModel,
            okDialogTitle = okDialogTitle,
            okDialogMessage = okDialogMessage
        )
    }
}


@Composable
private fun ContentForGuest() {
    CenteredScrollContainer(
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "You need to log in to see the settings menu.",
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                lineHeight = 35.sp)
        )
    }
}

@Composable
private fun ChangeEmailForm(
    userServiceViewModel: UserServiceViewModel,
    okDialogTitle: MutableState<String>,
    okDialogMessage: MutableState<String?>
) {
    var newEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isFormValid by remember {
        derivedStateOf {
            newEmail.isNotBlank() &&
                    password.isNotBlank() &&
                    userServiceViewModel.changeEmailState !is ApiState.Loading
        }
    }

    LaunchedEffect(userServiceViewModel.changeEmailState) {
        when (val apiState = userServiceViewModel.changeEmailState) {
            is ApiState.Success -> {
                okDialogTitle.value = "Success"
                okDialogMessage.value = apiState.data
            }
            is ApiState.Error -> {
                okDialogTitle.value = "Error"
                okDialogMessage.value = apiState.message
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = "Change email address",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                lineHeight = 35.sp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = newEmail,
            onValueChange = { newEmail = it },
            label = { Text("New Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth(),
            isError = newEmail.isNotBlank() &&
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                userServiceViewModel.changeEmail(
                    newEmail,
                    password
                )
            },
            modifier = Modifier
                .height(40.dp),
            enabled = isFormValid
        ) {
            Text(
                text = "Apply",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp)
            )
        }
    }
}