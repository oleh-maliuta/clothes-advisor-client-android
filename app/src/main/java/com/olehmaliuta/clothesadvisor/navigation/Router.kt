package com.olehmaliuta.clothesadvisor.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.currentBackStackEntryAsState

class Router(
    private val controller: NavHostController,
    private val stateHandlers: List<StateHandler>
) {
    fun navigate(
        route: String,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null,
        restoreAllApiStates: Boolean = true
    ) {
        val currentRoute = controller
            .currentBackStackEntry
            ?.destination
            ?.route

        if (currentRoute?.split('?')?.first() != route.split('?').first()) {
            controller.navigate(route, navOptions, navigatorExtras)
            if (restoreAllApiStates) {
                restoreState()
            }
        }
    }

    @Composable
    fun currentBackStackEntryAsState(): State<NavBackStackEntry?> {
        return controller.currentBackStackEntryAsState()
    }

    private fun restoreState() {
        stateHandlers.forEach { sh ->
            sh.restoreState()
        }
    }
}