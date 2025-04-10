package com.olehmaliuta.clothesadvisor.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.olehmaliuta.clothesadvisor.MainActivity
import com.olehmaliuta.clothesadvisor.api.http.security.AuthState
import com.olehmaliuta.clothesadvisor.api.http.view.PingServiceViewModel
import com.olehmaliuta.clothesadvisor.api.http.view.UserServiceViewModel
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.screens.*

@Composable
fun ScreenManager(activity: MainActivity) {
    val navController = rememberNavController()
    val router = remember { Router(navController) }
    val snackBarHostState = remember { SnackbarHostState() }
    val pingServiceViewModel: PingServiceViewModel = viewModel()
    val userServiceViewModel: UserServiceViewModel = viewModel {
        UserServiceViewModel(activity)
    }

    val screens = remember {
        mapOf<Screen, @Composable () -> Unit>(
            Screen.Registration to { RegistrationScreen(
                router = router,
                userServiceViewModel = userServiceViewModel
            ) },
            Screen.LogIn to { LogInScreen(
                router = router,
                userServiceViewModel = userServiceViewModel
            ) },
            Screen.ClothesList to { ClothesListScreen() },
            Screen.OutfitList to { OutfitListScreen() },
            Screen.EditCloth to { EditClothScreen() },
            Screen.EditOutfit to { EditOutfitScreen() },
            Screen.Analysis to { AnalysisScreen() },
            Screen.Statistics to { StatisticsScreen() },
            Screen.Settings to { SettingsScreen(
                userServiceViewModel = userServiceViewModel
            ) }
        )
    }

    navController.addOnDestinationChangedListener { _, _, _ ->
        userServiceViewModel.profile(
            locale = "en"
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (userServiceViewModel.authState == AuthState.Loading) {
                return@Scaffold
            }

            TopBar(
                context = activity,
                router = router,
                userServiceViewModel = userServiceViewModel
            )
        },
        bottomBar = {
            if (userServiceViewModel.authState == AuthState.Loading) {
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
                    when (val authState = userServiceViewModel.authState) {
                        AuthState.Loading -> LoadingDisplay()
                        is AuthState.Error -> ErrorDisplay(authState.message)
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
private fun ErrorDisplay(message: String) {
    CenteredScrollContainer(
        modifier = Modifier
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = message,
            textAlign = TextAlign.Justify,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold),
        )
    }
}