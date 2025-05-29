package com.olehmaliuta.clothesadvisor.utils.localization

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.core.content.edit
import java.util.Locale

class LanguageManager(private val context: Context) {
    companion object {
        private const val PREFS_NAME = "user"
        private const val KEY_SELECTED_LANGUAGE = "language"
        const val SYSTEM_DEFAULT_LANGUAGE = "system"
    }

    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setAppLanguage(languageCode: String) {
        sharedPrefs.edit { putString(KEY_SELECTED_LANGUAGE, languageCode) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                if (languageCode == SYSTEM_DEFAULT_LANGUAGE) {
                    LocaleList.getEmptyLocaleList()
                } else {
                    LocaleList.forLanguageTags(languageCode)
                }
        } else {
            applyLegacyLanguage(languageCode)
        }

        if (context is Activity) {
            context.recreate()
        }
    }

    fun getRealLanguage(): String {
        val systemLocale = Resources.getSystem().configuration.locales[0]
        return systemLocale.language.split("-").first()
    }

    fun getCurrentLanguage(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val systemLocales = context.getSystemService(LocaleManager::class.java)
                .applicationLocales
            if (systemLocales.isEmpty) {
                return SYSTEM_DEFAULT_LANGUAGE
            }
            return systemLocales[0]?.toLanguageTag()?.split("-")?.first()
                ?: getSavedLanguage()
        }

        return getSavedLanguage()
    }

    fun getSavedLanguage(): String {
        return sharedPrefs.getString(KEY_SELECTED_LANGUAGE, SYSTEM_DEFAULT_LANGUAGE)
            ?: SYSTEM_DEFAULT_LANGUAGE
    }

    fun applyProperConfiguration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val languageCode = getCurrentLanguage()
        if (languageCode != SYSTEM_DEFAULT_LANGUAGE) {
            applyLegacyLanguage(languageCode)
        }
    }

    private fun applyLegacyLanguage(languageCode: String) {
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