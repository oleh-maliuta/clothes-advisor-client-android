package com.olehmaliuta.clothesadvisor.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.olehmaliuta.clothesadvisor.App
import com.olehmaliuta.clothesadvisor.MainActivity
import com.olehmaliuta.clothesadvisor.api.http.security.AuthState
import com.olehmaliuta.clothesadvisor.api.http.security.AuthViewModel
import com.olehmaliuta.clothesadvisor.api.http.view.UserApiViewModel
import com.olehmaliuta.clothesadvisor.database.view.ClothingItemDaoViewModel
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.screens.*

@Composable
fun ScreenManager(activity: MainActivity) {
    val application = LocalContext.current.applicationContext as App

    // AUTHENTICATION
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(
            context = activity
        )
    )

    // REST API
    val userApiViewModel: UserApiViewModel = viewModel(
        factory = UserApiViewModel.Factory(
            clothingItemDaoRepository = application.clothingItemDaoRepository,
            context = activity
        )
    )

    // ROOM DATABASE
    val clothingItemDaoViewModel: ClothingItemDaoViewModel = viewModel(
        factory = ClothingItemDaoViewModel.Factory(
            clothingItemDaoRepository = application.clothingItemDaoRepository
        )
    )

    // NAVIGATION
    val navController = rememberNavController()
    val router = remember {
        Router(
            navController,
            listOf(
                userApiViewModel
            )
        )
    }

    // SNACK BAR
    val snackBarHostState = remember { SnackbarHostState() }

    // SIMPLE VARIABLES
    val authState = authViewModel.authState.value
    val screens = mapOf<Screen, @Composable () -> Unit>(
        Screen.Registration to { RegistrationScreen(
            router = router,
            userApiViewModel = userApiViewModel
        ) },
        Screen.LogIn to { LogInScreen(
            router = router,
            userApiViewModel = userApiViewModel
        ) },
        Screen.ClothesList to { ClothesListScreen() },
        Screen.OutfitList to { OutfitListScreen() },
        Screen.EditCloth to { EditClothScreen() },
        Screen.EditOutfit to { EditOutfitScreen() },
        Screen.Analysis to { AnalysisScreen() },
        Screen.Statistics to { StatisticsScreen() },
        Screen.Settings to { SettingsScreen(
            router = router,
            authViewModel = authViewModel,
            userApiViewModel = userApiViewModel
        ) }
    )

    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            authViewModel.profile()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (
                authState is AuthState.Loading ||
                authState is AuthState.Error
                ) {
                return@Scaffold
            }

            TopBar(
                router = router,
                authViewModel = authViewModel
            )
        },
        bottomBar = {
            if (
                authState is AuthState.Loading ||
                authState is AuthState.Error
                ) {
                return@Scaffold
            }

            BottomBar(
                router = router,
                navItems = activity.navItems
            )
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.ClothesList.name,
            modifier = Modifier.padding(paddingValues)
        ) {
            screens.forEach { (route, screen) ->
                composable(route.name) {
                    when (authState) {
                        AuthState.Loading -> LoadingDisplay()
                        is AuthState.Error -> ErrorDisplay(
                            authViewModel = authViewModel,
                            message = authState.message
                        )
                        else -> screen()
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingDisplay() {
    CenteredScrollContainer(
        modifier = Modifier
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = "Loading...",
            textAlign = TextAlign.Justify,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
private fun ErrorDisplay(
    authViewModel: AuthViewModel,
    message: String
) {
    CenteredScrollContainer(
        modifier = Modifier
            .padding(horizontal = 10.dp)
    ) {
        Column {
            Text(
                text = message,
                textAlign = TextAlign.Justify,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold),
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = { authViewModel.profile() },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            ) {
                Text(
                    text = "Try again",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(0.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = { authViewModel.logOut() },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
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
}