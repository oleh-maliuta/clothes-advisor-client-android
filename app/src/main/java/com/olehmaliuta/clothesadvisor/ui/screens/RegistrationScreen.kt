package com.olehmaliuta.clothesadvisor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.data.http.security.ApiState
import com.olehmaliuta.clothesadvisor.ui.viewmodels.UserViewModel
import com.olehmaliuta.clothesadvisor.ui.components.CenteredScrollContainer
import com.olehmaliuta.clothesadvisor.ui.components.InfoDialog
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.utils.LocaleConstants
import java.util.Locale

@Composable
fun RegistrationScreen(
    router: Router,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current

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
        title = if (success)
            stringResource(R.string.registration__success_message_title) else
                stringResource(R.string.registration__error_message_title),
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
                text = stringResource(R.string.registration__header),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(stringResource(R.string.registration__email_input))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email__input")
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(stringResource(R.string.registration__password_input))
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password__input")
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = {
                    Text(stringResource(R.string.registration__confirm_password_input))
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = !passwordsMatch && confirmPassword.isNotBlank(),
                supportingText = {
                    if (!passwordsMatch && confirmPassword.isNotBlank()) {
                        Text(
                            text = stringResource(
                                R.string.registration__passwords_don_t_match_message
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("confirm_password__input")
            )

            Button(
                onClick = {
                    userViewModel.register(
                        context = context,
                        email = email,
                        password = password,
                        locale = LocaleConstants.getSecondLangCodeByFirst(
                            Locale.getDefault().language)
                )},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("apply__button"),
                enabled = isFormValid
            ) {
                if (userViewModel.registrationState is ApiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.registration__apply_button),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}