package com.olehmaliuta.clothesadvisor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

class Router {
    private val controller: NavController

    constructor(controller: NavController) {
        this.controller = controller
    }

    fun getController(): NavController {
        return controller
    }

    fun navigateTo(route: Screen) {
        controller.navigate(route.name) {
            popUpTo(controller.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}