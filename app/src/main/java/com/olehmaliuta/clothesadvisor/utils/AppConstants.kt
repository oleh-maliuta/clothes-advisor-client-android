package com.olehmaliuta.clothesadvisor.utils

import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.ui.navigation.NavItem
import com.olehmaliuta.clothesadvisor.ui.navigation.Screen

object AppConstants {
    val navItems = listOf<NavItem>(
        NavItem(
            route = Screen.ClothesList,
            label = "Clothes",
            iconId = R.drawable.cloth
        ),
        NavItem(
            route = Screen.OutfitList,
            label = "Outfits",
            iconId = R.drawable.outfit
        ),
        NavItem(
            route = Screen.Generate,
            label = "Generate",
            iconId = R.drawable.generate
        ),
        NavItem(
            route = Screen.Statistics,
            label = "Statistics",
            iconId = R.drawable.statistics
        ),
        NavItem(
            route = Screen.Settings,
            label = "Settings",
            iconId = R.drawable.settings
        )
    )
}