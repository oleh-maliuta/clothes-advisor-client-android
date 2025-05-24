package com.olehmaliuta.clothesadvisor

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.olehmaliuta.clothesadvisor.ui.components.ScreenManager
import com.olehmaliuta.clothesadvisor.ui.theme.ClothesAdvisorTheme

class MainActivity : LocaleAwareActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClothesAdvisorTheme {
                ScreenManager()
            }
        }
    }
}