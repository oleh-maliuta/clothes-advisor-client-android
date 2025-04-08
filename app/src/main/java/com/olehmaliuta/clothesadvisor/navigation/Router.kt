package com.olehmaliuta.clothesadvisor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

class Router {
    var controller: NavController
        private set

    constructor(controller: NavController) {
        this.controller = controller
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