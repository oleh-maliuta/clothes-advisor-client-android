package com.olehmaliuta.clothesadvisor

import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import com.olehmaliuta.clothesadvisor.ui.LanguageManager
import java.util.Locale

abstract class LocaleAwareActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val app = newBase.applicationContext as App
        val languageCode = app.languageManager.getCurrentLanguage()

        super.attachBaseContext(
            if (languageCode == LanguageManager.SYSTEM_DEFAULT_LANGUAGE) {
                newBase
            } else {
                wrapContext(newBase, languageCode)
            }
        )
    }

    private fun wrapContext(context: Context, languageCode: String): Context {
        val locale = Locale.forLanguageTag(languageCode)
        val config = context.resources.configuration
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    override fun onResume() {
        super.onResume()
        checkForSystemLanguageChange()
    }

    private fun checkForSystemLanguageChange() {
        val app = applicationContext as App
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val currentSystemLang = app.languageManager.getCurrentLanguage()
            val savedLang = app.languageManager.getSavedLanguage()
            if (currentSystemLang != savedLang) {
                app.languageManager.setAppLanguage(currentSystemLang)
            }
        }
    }
}