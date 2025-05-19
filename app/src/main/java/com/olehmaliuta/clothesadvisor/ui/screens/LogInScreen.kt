package com.olehmaliuta.clothesadvisor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.data.http.security.ApiState
import com.olehmaliuta.clothesadvisor.ui.viewmodels.UserViewModel
import com.olehmaliuta.clothesadvisor.ui.components.AcceptCancelDialog
import com.olehmaliuta.clothesadvisor.ui.components.CenteredScrollContainer
import com.olehmaliuta.clothesadvisor.ui.components.InfoDialog
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.utils.LocaleConstants
import java.util.Locale

@Composable
fun LogInScreen(
    router: Router,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailToRestorePassword by remember { mutableStateOf("") }
    var syncByServerData by remember { mutableStateOf(true) }
    var okDialogTitle by remember { mutableStateOf("") }
    var okDialogMessage by remember { mutableStateOf<String?>(null) }
    var isForgotPasswordDialogOpen by remember { mutableStateOf(false) }

    val isFormValid by remember {
        derivedStateOf {
            email.isNotBlank() &&
                    password.isNotBlank() &&
                    userViewModel.logInState !is ApiState.Loading
        }
    }

    val errorMessageTitle = stringResource(R.string.registration__error_message_title)
    val forgotPasswordEmailIsSentTitle = stringResource(R.string.authorization__forgot_password_email_is_sent_title)

    LaunchedEffect(userViewModel.logInState) {
        when (val apiState = userViewModel.logInState) {
            is ApiState.Success -> {
                router.navigate(
                    route = Screen.ClothesList.name,
                    apiStatesToRestore = listOf(userViewModel)
                )
            }
            is ApiState.Error -> {
                okDialogTitle = errorMessageTitle
                okDialogMessage = apiState.message
            }
            else -> {}
        }
    }

    LaunchedEffect(userViewModel.forgotPasswordState) {
        when (val apiState = userViewModel.forgotPasswordState) {
            is ApiState.Success -> {
                isForgotPasswordDialogOpen = false
                okDialogTitle = forgotPasswordEmailIsSentTitle
                okDialogMessage = apiState.data
            }
            is ApiState.Error -> {
                isForgotPasswordDialogOpen = false
                okDialogTitle = errorMessageTitle
                okDialogMessage = apiState.message
            }
            else -> {}
        }
    }

    InfoDialog(
        title = okDialogTitle,
        content = okDialogMessage,
        onConfirm = {
            okDialogMessage = null
        }
    )

    AcceptCancelDialog(
        isOpen = isForgotPasswordDialogOpen,
        title = stringResource(R.string.authorization__forgot_password_title),
        onDismiss = {
            isForgotPasswordDialogOpen = false
        },
        onAccept = {
            userViewModel.forgotPassword(
                email = emailToRestorePassword,
                locale = LocaleConstants.getSecondLangCodeByFirst(
                    Locale.getDefault().language)
            )
        },
        acceptText = stringResource(R.string.authorization__forgot_password_apply_button),
        acceptEnabled =
            userViewModel.forgotPasswordState !is ApiState.Loading &&
            emailToRestorePassword.isNotBlank()
    ) {
        Column {
            Text(
                text = stringResource(R.string.authorization__forgot_password_description),
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = emailToRestorePassword,
                onValueChange = { emailToRestorePassword = it },
                label = { Text(stringResource(R.string.authorization__email_input)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }
    }

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
                text = stringResource(R.string.authorization__header),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.authorization__email_input)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.authorization__password_input)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    TextButton(
                        onClick = { isForgotPasswordDialogOpen = true },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            stringResource(R.string.authorization__forgot_password_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )

            Spacer(Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    checked = syncByServerData,
                    onCheckedChange =  { syncByServerData = !syncByServerData },
                    modifier = Modifier.semantics {
                        this.contentDescription =
                            if (syncByServerData) "Checked" else "Unchecked"
                    }
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.authorization__sync_by_the_server_data_switch),
                    fontSize = 17.sp
                )
            }

            Spacer(Modifier.height(2.dp))

            Button(
                onClick = {
                    userViewModel.logIn(
                        email = email,
                        password = password,
                        syncByServerData = syncByServerData,
                        context = context
                    )},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = isFormValid
            ) {
                if (userViewModel.logInState is ApiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.authorization__apply_button),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}