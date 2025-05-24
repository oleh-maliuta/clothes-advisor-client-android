package com.olehmaliuta.clothesadvisor.ui

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

class LanguageManager {
    companion object {
        const val SYSTEM_DEFAULT_LANGUAGE = "system"
    }

    fun changeLanguage(context: Context, languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                if (languageCode == SYSTEM_DEFAULT_LANGUAGE) {
                    LocaleList.getEmptyLocaleList()
                } else {
                    LocaleList.forLanguageTags(languageCode)
                }
        } else {
            AppCompatDelegate.setApplicationLocales(
                if (languageCode == SYSTEM_DEFAULT_LANGUAGE) {
                    LocaleListCompat.getEmptyLocaleList()
                } else {
                    LocaleListCompat.forLanguageTags(languageCode)
                }
            )

            updateResourcesLegacy(context, languageCode)
        }

        if (context is android.app.Activity) {
            context.recreate()
        }
    }

    fun getLanguageCode(context: Context): String {
        val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales[0]
        } else {
            AppCompatDelegate.getApplicationLocales()[0]
        }

        return if (currentLocale == null) {
            SYSTEM_DEFAULT_LANGUAGE
        } else {
            currentLocale.toLanguageTag().split("-").first()
        }
    }

    private fun updateResourcesLegacy(context: Context, languageCode: String) {
        val locale = if (languageCode == SYSTEM_DEFAULT_LANGUAGE) {
            Locale.getDefault()
        } else {
            Locale.forLanguageTag(languageCode)
        }

        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}