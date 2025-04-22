package com.olehmaliuta.clothesadvisor.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import com.olehmaliuta.clothesadvisor.viewmodels.AuthViewModel
import com.olehmaliuta.clothesadvisor.viewmodels.UserViewModel
import com.olehmaliuta.clothesadvisor.components.CenteredScrollContainer
import com.olehmaliuta.clothesadvisor.components.OkDialog
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen

@Composable
fun SettingsScreen(
    router: Router,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    when (authViewModel.authState.value) {
        is AuthState.Authenticated -> {
            ContentForUser(
                router = router,
                authViewModel = authViewModel,
                userViewModel = userViewModel
            )
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
private fun ContentForUser(
    router: Router,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    var okDialogTitle = remember { mutableStateOf("") }
    var okDialogMessage = remember { mutableStateOf<String?>(null) }

    OkDialog(
        title = okDialogTitle.value,
        content = okDialogMessage.value,
        onConfirm = {
            okDialogMessage.value = null

            if (userViewModel.changeEmailState is ApiState.Success) {
                authViewModel.logOut()
                router.navigate(
                    route = Screen.LogIn.name,
                    apiStatesToRestore = listOf(userViewModel)
                )
            }
        }
    )

    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        UserAccountSection(
            authViewModel = authViewModel
        )

        Spacer(modifier = Modifier.height(30.dp))

        ChangeEmailForm(
            userViewModel = userViewModel,
            okDialogTitle = okDialogTitle,
            okDialogMessage = okDialogMessage
        )

        Spacer(modifier = Modifier.height(30.dp))

        ChangePasswordForm(
            userViewModel = userViewModel,
            okDialogTitle = okDialogTitle,
            okDialogMessage = okDialogMessage
        )

        Spacer(modifier = Modifier.height(15.dp))
    }
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
                text = "You need to log in to see the settings menu.",
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

@Composable
private fun UserAccountSection(
    authViewModel: AuthViewModel
) {
    val currentUser = (authViewModel.authState.value as? AuthState.Authenticated)?.user

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Account",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                lineHeight = 35.sp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Logged in as:",
            style = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        )

        Text(
            text = currentUser?.email ?: "Unknown",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                authViewModel.logOut()
            },
            modifier = Modifier
                .height(40.dp)
        ) {
            Text(
                text = "Log Out",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp)
            )
        }
    }
}

@Composable
private fun ChangeEmailForm(
    userViewModel: UserViewModel,
    okDialogTitle: MutableState<String>,
    okDialogMessage: MutableState<String?>
) {
    var newEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isFormValid by remember {
        derivedStateOf {
            newEmail.isNotBlank() &&
                    password.isNotBlank() &&
                    userViewModel.changeEmailState !is ApiState.Loading &&
                    userViewModel.changePasswordState !is ApiState.Loading
        }
    }

    LaunchedEffect(userViewModel.changeEmailState) {
        when (val apiState = userViewModel.changeEmailState) {
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
                userViewModel.changeEmail(
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

@Composable
private fun ChangePasswordForm(
    userViewModel: UserViewModel,
    okDialogTitle: MutableState<String>,
    okDialogMessage: MutableState<String?>
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    val passwordsMatch by remember {
        derivedStateOf { newPassword == confirmNewPassword }
    }
    val isFormValid by remember {
        derivedStateOf {
            oldPassword.isNotBlank() &&
                    newPassword.isNotBlank() &&
                    passwordsMatch &&
                    userViewModel.changeEmailState !is ApiState.Loading &&
                    userViewModel.changePasswordState !is ApiState.Loading
        }
    }

    LaunchedEffect(userViewModel.changePasswordState) {
        when (val apiState = userViewModel.changePasswordState) {
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
            text = "Change password",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                lineHeight = 35.sp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("Old Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it },
            label = { Text("Confirm New Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                userViewModel.changePassword(
                    oldPassword,
                    newPassword
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