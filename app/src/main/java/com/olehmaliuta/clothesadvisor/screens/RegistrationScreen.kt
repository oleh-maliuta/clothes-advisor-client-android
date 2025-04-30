package com.olehmaliuta.clothesadvisor.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.api.http.security.ApiState
import com.olehmaliuta.clothesadvisor.viewmodels.UserViewModel
import com.olehmaliuta.clothesadvisor.components.CenteredScrollContainer
import com.olehmaliuta.clothesadvisor.components.InfoDialog
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen

@Composable
fun RegistrationScreen(
    router: Router,
    userViewModel: UserViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<Boolean>(false) }

    val passwordsMatch by remember {
        derivedStateOf { password == confirmPassword }
    }
    val isFormValid by remember {
        derivedStateOf {
            email.isNotBlank() &&
                    password.isNotBlank() &&
                    passwordsMatch &&
                    userViewModel.registrationState !is ApiState.Loading
        }
    }

    LaunchedEffect(userViewModel.registrationState) {
        when (val apiState = userViewModel.registrationState) {
            is ApiState.Success -> {
                dialogMessage = apiState.data
                success = true
            }
            is ApiState.Error -> {
                dialogMessage = apiState.message
            }
            else -> {}
        }
    }

    InfoDialog(
        title = if (success) "Success" else "Error",
        content = dialogMessage,
        onConfirm = {
            dialogMessage = null
            if (success) {
                success = false
                router.navigate(
                    route = Screen.LogIn.name,
                    apiStatesToRestore = listOf(userViewModel)
                )
            }
        }
    )

    CenteredScrollContainer(
        modifier = Modifier
            .padding(vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create Account",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = !passwordsMatch && confirmPassword.isNotBlank(),
                supportingText = {
                    if (!passwordsMatch && confirmPassword.isNotBlank()) {
                        Text("Passwords don't match")
                    }
                }
            )

            Button(
                onClick = {
                    userViewModel.register(
                        email = email,
                        password = password
                )},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = isFormValid
            ) {
                Text(
                    text = if (
                        userViewModel.registrationState == ApiState.Loading)
                        "..." else "Register",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp)
                )
            }
        }
    }
}