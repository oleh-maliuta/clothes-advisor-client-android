package com.olehmaliuta.clothesadvisor.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
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
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.data.http.security.AuthState
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.snackbar.SnackbarHandler
import com.olehmaliuta.clothesadvisor.ui.screens.ClothesListScreen
import com.olehmaliuta.clothesadvisor.ui.screens.EditClothingItemScreen
import com.olehmaliuta.clothesadvisor.ui.screens.EditOutfitScreen
import com.olehmaliuta.clothesadvisor.ui.screens.GeneratingScreen
import com.olehmaliuta.clothesadvisor.ui.screens.LogInScreen
import com.olehmaliuta.clothesadvisor.ui.screens.OutfitListScreen
import com.olehmaliuta.clothesadvisor.ui.screens.RegistrationScreen
import com.olehmaliuta.clothesadvisor.ui.screens.SettingsScreen
import com.olehmaliuta.clothesadvisor.ui.screens.StatisticsScreen
import com.olehmaliuta.clothesadvisor.ui.viewmodels.AuthViewModel
import com.olehmaliuta.clothesadvisor.ui.viewmodels.ClothingItemViewModel
import com.olehmaliuta.clothesadvisor.ui.viewmodels.OutfitViewModel
import com.olehmaliuta.clothesadvisor.ui.viewmodels.RecommendationViewModel
import com.olehmaliuta.clothesadvisor.ui.viewmodels.StatisticsViewModel
import com.olehmaliuta.clothesadvisor.ui.viewmodels.UserViewModel

@Composable
fun ScreenManager() {
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
    val statisticsViewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModel.Factory(
            repository = application.statisticsDaoRepository
        )
    )
    val recommendationViewModel: RecommendationViewModel = viewModel(
        factory = RecommendationViewModel.Factory(
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
                outfitViewModel,
                statisticsViewModel,
                recommendationViewModel
            )
        )
    }

    // SIMPLE VARIABLES
    val authState = authViewModel.authState.value
    val screens = mapOf<Screen, @Composable () -> Unit>(
        Screen.Registration to {
            RegistrationScreen(
                router = router,
                userViewModel = userViewModel
            )
        },
        Screen.LogIn to {
            LogInScreen(
                router = router,
                userViewModel = userViewModel
            )
        },
        Screen.ClothesList to {
            ClothesListScreen(
                router = router,
                clothingItemViewModel = clothingItemViewModel
            )
        },
        Screen.OutfitList to {
            OutfitListScreen(
                router = router,
                outfitViewModel = outfitViewModel
            )
        },
        Screen.EditClothingItem to {
            EditClothingItemScreen(
                router = router,
                authViewModel = authViewModel,
                clothingItemViewModel = clothingItemViewModel
            )
        },
        Screen.EditOutfit to {
            EditOutfitScreen(
                router = router,
                clothingItemViewModel = clothingItemViewModel,
                outfitViewModel = outfitViewModel
            )
        },
        Screen.Generate to {
            GeneratingScreen(
                router = router,
                authViewModel = authViewModel,
                outfitViewModel = outfitViewModel,
                recommendationViewModel = recommendationViewModel
            )
        },
        Screen.Statistics to {
            StatisticsScreen(
                statisticsViewModel = statisticsViewModel
            )
        },
        Screen.Settings to {
            SettingsScreen(
                router = router,
                authViewModel = authViewModel,
                userViewModel = userViewModel
            )
        }
    )

    // SIDE EFFECTS
    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            authViewModel.profile(
                context = context
            )
        }
    }

    // SCREEN SYSTEM
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
                router = router
            )
        },
        snackbarHost = { SnackbarHandler() }
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
                        else -> {
                            Box(
                                modifier = Modifier
                                    .testTag("screen__${route.name}")
                            )
                            screen()
                        }
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
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorDisplay(
    authViewModel: AuthViewModel,
    message: String
) {
    val context = LocalContext.current

    CenteredScrollContainer(
        modifier = Modifier
            .padding(horizontal = 10.dp)
    ) {
        Column {
            Text(
                text = message,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(
                onClick = {
                    authViewModel.profile(
                        context = context
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = stringResource(R.string.error__try_again_button),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { authViewModel.logOut() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = stringResource(R.string.error__enter_as_guest_button),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}