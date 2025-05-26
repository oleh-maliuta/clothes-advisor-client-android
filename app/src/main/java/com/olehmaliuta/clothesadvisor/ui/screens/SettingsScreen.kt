package com.olehmaliuta.clothesadvisor.ui.screens

import android.util.Patterns
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.App
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.data.http.security.ApiState
import com.olehmaliuta.clothesadvisor.data.http.security.AuthState
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.ui.components.InfoDialog
import com.olehmaliuta.clothesadvisor.ui.viewmodels.AuthViewModel
import com.olehmaliuta.clothesadvisor.ui.viewmodels.UserViewModel
import com.olehmaliuta.clothesadvisor.utils.AppConstants
import com.olehmaliuta.clothesadvisor.utils.LocaleConstants
import com.olehmaliuta.clothesadvisor.utils.findActivity
import java.util.Locale

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
                router = router,
                authViewModel = authViewModel
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

    InfoDialog(
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
            router = router,
            authViewModel = authViewModel
        )

        Spacer(modifier = Modifier.height(30.dp))

        PersonalizationForm()

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
    router: Router,
    authViewModel: AuthViewModel
) {
    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        UserAccountSection(
            router = router,
            authViewModel = authViewModel
        )

        Spacer(modifier = Modifier.height(30.dp))

        PersonalizationForm()

        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Composable
private fun UserAccountSection(
    router: Router,
    authViewModel: AuthViewModel
) {
    val currentUser = (authViewModel.authState.value as? AuthState.Authenticated)?.user

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.settings__account__title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            lineHeight = 35.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (currentUser == null) {
            Button(
                onClick = {
                    router.navigate(Screen.LogIn.name)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("log_in__button")
            ) {
                Text(
                    text = stringResource(R.string.settings__guest__log_in_button),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    router.navigate(Screen.Registration.name)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("sign_up__button")
            ) {
                Text(
                    text = stringResource(R.string.settings__guest__sign_up_button),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        } else {
            Text(
                text = stringResource(R.string.settings__account__label),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = currentUser.email ?: "Unknown",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
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
                    text = stringResource(R.string.settings__account__log_out_button),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun PersonalizationForm() {
    val context = LocalContext.current
    val languageManager = (context.applicationContext as App).languageManager

    var isLanguageMenuOpen by remember { mutableStateOf(false) }

    val selectedLanguage by remember {
        derivedStateOf { languageManager.getCurrentLanguage() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.settings__personalization__title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            lineHeight = 35.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = stringResource(AppConstants.languages.getValue(selectedLanguage)),
                onValueChange = {},
                label = { Text(stringResource(R.string.settings__personalization__language__title)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                },
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    isLanguageMenuOpen = true
                                }
                            }
                        }
                    }
            )
            DropdownMenu(
                expanded = isLanguageMenuOpen,
                onDismissRequest = { isLanguageMenuOpen = false }
            ) {
                AppConstants.languages.forEach { languageOption ->
                    DropdownMenuItem(
                        text = { Text(stringResource(languageOption.value)) },
                        onClick = {
                            isLanguageMenuOpen = false
                            languageManager.setAppLanguage(languageOption.key)
                            context.findActivity()?.recreate()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChangeEmailForm(
    userViewModel: UserViewModel,
    okDialogTitle: MutableState<String>,
    okDialogMessage: MutableState<String?>
) {
    val context = LocalContext.current

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

    val successMessageTitle = stringResource(R.string.settings__success_message_title)
    val errorMessageTitle = stringResource(R.string.settings__error_message_title)

    LaunchedEffect(userViewModel.changeEmailState) {
        when (val apiState = userViewModel.changeEmailState) {
            is ApiState.Success -> {
                okDialogTitle.value = successMessageTitle
                okDialogMessage.value = apiState.data
            }
            is ApiState.Error -> {
                okDialogTitle.value = errorMessageTitle
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
            text = stringResource(R.string.settings__change_email__title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            lineHeight = 35.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = newEmail,
            onValueChange = { newEmail = it },
            label = { Text(stringResource(R.string.settings__change_email__new_email_input)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth(),
            isError = newEmail.isNotBlank() &&
                    !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.settings__change_email__password_input)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                userViewModel.changeEmail(
                    context,
                    newEmail,
                    password,
                    locale = LocaleConstants.getSecondLangCodeByFirst(
                        Locale.getDefault().language)
                )
            },
            modifier = Modifier
                .height(40.dp),
            enabled = isFormValid
        ) {
            if (userViewModel.changeEmailState is ApiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.settings__change_email__apply_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun ChangePasswordForm(
    userViewModel: UserViewModel,
    okDialogTitle: MutableState<String>,
    okDialogMessage: MutableState<String?>
) {
    val context = LocalContext.current

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

    val successMessageTitle = stringResource(R.string.settings__success_message_title)
    val errorMessageTitle = stringResource(R.string.settings__error_message_title)

    LaunchedEffect(userViewModel.changePasswordState) {
        when (val apiState = userViewModel.changePasswordState) {
            is ApiState.Success -> {
                okDialogTitle.value = successMessageTitle
                okDialogMessage.value = apiState.data
            }
            is ApiState.Error -> {
                okDialogTitle.value = errorMessageTitle
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
            text = stringResource(R.string.settings__change_password__title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            lineHeight = 35.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text(stringResource(R.string.settings__change_password__old_password_input)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text(stringResource(R.string.settings__change_password__new_password_input)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it },
            label = { Text(stringResource(R.string.settings__change_password__confirm_new_password_input)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                userViewModel.changePassword(
                    context,
                    oldPassword,
                    newPassword
                )
            },
            modifier = Modifier
                .height(40.dp),
            enabled = isFormValid
        ) {
            if (userViewModel.changePasswordState is ApiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.settings__change_password__apply_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}