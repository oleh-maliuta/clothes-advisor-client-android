package com.olehmaliuta.clothesadvisor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olehmaliuta.clothesadvisor.api.http.view.PingServiceViewModel
import com.olehmaliuta.clothesadvisor.components.ScreenManager
import com.olehmaliuta.clothesadvisor.navigation.NavItem
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.ui.theme.ClothesAdvisorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClothesAdvisorTheme {
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
                        route = Screen.Analysis,
                        label = "Analysis",
                        iconId = R.drawable.analysis
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

                ScreenManager(this, navItems)
            }
        }
    }
}