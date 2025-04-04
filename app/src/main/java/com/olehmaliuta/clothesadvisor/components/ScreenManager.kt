package com.olehmaliuta.clothesadvisor.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.olehmaliuta.clothesadvisor.MainActivity
import com.olehmaliuta.clothesadvisor.navigation.NavItem
import com.olehmaliuta.clothesadvisor.navigation.Router
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.screens.*

@Composable
fun ScreenManager(activity: MainActivity, navItems: List<NavItem>) {
    val navController = rememberNavController()
    val router = Router(navController)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(router) },
        bottomBar = { BottomBar(router, navItems) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Registration.name,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = Screen.Registration.name) {
                RegistrationScreen()
            }
            composable(route = Screen.LogIn.name) {
                LogInScreen(navController)
            }
            composable(route = Screen.ClothesList.name) {
                ClothesListScreen(navController)
            }
            composable(route = Screen.OutfitList.name) {
                OutfitListScreen(navController)
            }
            composable(route = Screen.EditCloth.name) {
                EditClothScreen(navController)
            }
            composable(route = Screen.EditOutfit.name) {
                EditOutfitScreen(navController)
            }
            composable(route = Screen.Analysis.name) {
                AnalysisScreen(navController)
            }
            composable(route = Screen.Statistics.name) {
                StatisticsScreen(navController)
            }
            composable(route = Screen.Settings.name) {
                SettingsScreen(navController)
            }
        }
    }
}