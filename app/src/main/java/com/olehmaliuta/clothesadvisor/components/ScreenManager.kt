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
import com.olehmaliuta.clothesadvisor.api.http.security.AuthState
import com.olehmaliuta.clothesadvisor.navigation.NavItem
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.screens.*
import com.olehmaliuta.clothesadvisor.viewmodels.*

@Composable
fun ScreenManager(navItems: List<NavItem>) {
    val context = LocalContext.current
    val application = context.applicationContext as App
    val startDestination = Screen.ClothesList.name

    // VIEW MODELS
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(
            clothingItemDaoRepository = application.clothingItemDaoRepository,
            outfitDaoRepository = application.outfitDaoRepository,
            context = context
        )
    )
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.Factory(
            clothingItemDaoRepository = application.clothingItemDaoRepository,
            outfitDaoRepository = application.outfitDaoRepository,
            context = context
        )
    )
    val clothingItemViewModel: ClothingItemViewModel = viewModel(
        factory = ClothingItemViewModel.Factory(
            repository = application.clothingItemDaoRepository,
            context = context
        )
    )
    val outfitViewModel: OutfitViewModel = viewModel(
        factory = OutfitViewModel.Factory(
            repository = application.outfitDaoRepository,
            context = context
        )
    )

    // NAVIGATION
    val navController = rememberNavController()
    val router = remember {
        Router(
            navController,
            startDestination,
            listOf(
                userViewModel,
                clothingItemViewModel,
                outfitViewModel
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
            userViewModel = userViewModel
        ) },
        Screen.LogIn to { LogInScreen(
            router = router,
            userViewModel = userViewModel
        ) },
        Screen.ClothesList to { ClothesListScreen(
            router = router,
            clothingItemViewModel = clothingItemViewModel
        ) },
        Screen.OutfitList to { OutfitListScreen(
            router = router,
            outfitViewModel = outfitViewModel
        ) },
        Screen.EditClothingItem to { EditClothingItemScreen(
            router = router,
            authViewModel = authViewModel,
            clothingItemViewModel = clothingItemViewModel
        ) },
        Screen.EditOutfit to { EditOutfitScreen(
            router = router,
            clothingItemViewModel = clothingItemViewModel,
            outfitViewModel = outfitViewModel
        ) },
        Screen.Analysis to { AnalysisScreen(
            router = router,
            authViewModel = authViewModel
        ) },
        Screen.Statistics to { StatisticsScreen() },
        Screen.Settings to { SettingsScreen(
            router = router,
            authViewModel = authViewModel,
            userViewModel = userViewModel
        ) }
    )

    // SIDE EFFECTS
    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            authViewModel.profile()
        }
    }

    // IMPLEMENTATION
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (
                authState is AuthState.Loading ||
                authState is AuthState.Error
                ) {
                return@Scaffold
            }

            BottomBar(
                router = router,
                navItems = navItems
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
                    text = "Enter as a guest",
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