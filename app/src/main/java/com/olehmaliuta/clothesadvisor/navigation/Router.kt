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
    private val defaultStateHandlers: List<StateHandler>
) {
    fun navigate(
        route: String,
        noHistory: Boolean = false,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null,
        apiStatesToRestore: List<StateHandler>? = null
    ) {
        val currentRoute = controller
            .currentBackStackEntry
            ?.destination
            ?.route

        if (currentRoute?.split('?')?.first() != route.split('?').first()) {
            if (noHistory) {
                controller.navigate(route) {
                    popUpTo(controller.graph.startDestinationId)
                    launchSingleTop = true
                }
            } else {
                controller.navigate(route, navOptions, navigatorExtras)
            }
            restoreState(apiStatesToRestore)
        }
    }

    @Composable
    fun currentBackStackEntryAsState(): State<NavBackStackEntry?> {
        return controller.currentBackStackEntryAsState()
    }

    private fun restoreState(
        apiStatesToRestore: List<StateHandler>?
    ) {
        val states = apiStatesToRestore ?: defaultStateHandlers
        states.forEach { state ->
            state.restoreState()
        }
    }
}