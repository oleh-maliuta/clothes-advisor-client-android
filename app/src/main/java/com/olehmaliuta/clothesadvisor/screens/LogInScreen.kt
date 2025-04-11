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
import androidx.compose.material3.TextButton
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
import com.olehmaliuta.clothesadvisor.api.http.view.UserServiceViewModel
import com.olehmaliuta.clothesadvisor.components.CenteredScrollContainer
import com.olehmaliuta.clothesadvisor.components.OkDialog
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen

@Composable
fun LogInScreen(
    router: Router,
    userServiceViewModel: UserServiceViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf<String?>(null) }

    val isFormValid by remember {
        derivedStateOf {
            email.isNotBlank() &&
                    password.isNotBlank() &&
                    userServiceViewModel.logInState !is ApiState.Loading
        }
    }

    LaunchedEffect(userServiceViewModel.logInState) {
        when (val apiState = userServiceViewModel.logInState) {
            is ApiState.Success -> {
                router.navigate(Screen.ClothesList.name)
                userServiceViewModel.logInState
            }
            is ApiState.Error -> {
                dialogMessage = apiState.message
            }
            else -> {}
        }
    }

    OkDialog(
        title = "Error",
        content = dialogMessage,
        onConfirm = {
            dialogMessage = null
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
                text = "Log in",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold),
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
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                trailingIcon = {
                    TextButton(
                        onClick = { /* ... */ },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            "Forgot?",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )

            Button(
                onClick = {
                    userServiceViewModel.logIn(
                        email = email,
                        password = password,
                        locale = "en"
                    )},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = isFormValid
            ) {
                Text(
                    text = if (
                        userServiceViewModel.logInState == ApiState.Loading)
                        "..." else "Log in",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp)
                )
            }
        }
    }
}