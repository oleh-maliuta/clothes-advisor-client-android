package com.olehmaliuta.clothesadvisor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.olehmaliuta.clothesadvisor.components.ScreenNavigator
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
                        route = Screen.ClothesList.name,
                        label = "Clothes",
                        iconId = R.drawable.cloth
                    ),
                    NavItem(
                        route = Screen.OutfitList.name,
                        label = "Outfits",
                        iconId = R.drawable.outfit
                    ),
                    NavItem(
                        route = Screen.Analysis.name,
                        label = "Analysis",
                        iconId = R.drawable.analysis
                    ),
                    NavItem(
                        route = Screen.Settings.name,
                        label = "Settings",
                        iconId = R.drawable.settings
                    )
                )

                ScreenNavigator(navItems)
            }
        }
    }
}