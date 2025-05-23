package com.olehmaliuta.clothesadvisor.ui

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class LanguageManager {
    companion object {
        const val SYSTEM_DEFAULT_LANGUAGE = "system"
    }

    fun changeLanguage(context: Context, languageCode: String) {
        when {
            languageCode == SYSTEM_DEFAULT_LANGUAGE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.getSystemService(LocaleManager::class.java)
                        .applicationLocales = LocaleList.getEmptyLocaleList()
                } else {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                context.getSystemService(LocaleManager::class.java)
                    .applicationLocales = LocaleList.forLanguageTags(languageCode)
            }
            else -> {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
            }
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
}