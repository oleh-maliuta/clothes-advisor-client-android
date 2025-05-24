package com.olehmaliuta.clothesadvisor

import android.app.Application
import android.content.Context
import com.olehmaliuta.clothesadvisor.data.database.AppDb
import com.olehmaliuta.clothesadvisor.data.database.repositories.ClothingItemDaoRepository
import com.olehmaliuta.clothesadvisor.data.database.repositories.OutfitDaoRepository
import com.olehmaliuta.clothesadvisor.data.database.repositories.StatisticsDaoRepository
import com.olehmaliuta.clothesadvisor.ui.LanguageManager
import java.util.Locale

class App : Application() {
    val database by lazy {
        AppDb.getDatabase(this)
    }
    val clothingItemDaoRepository by lazy {
        ClothingItemDaoRepository(database.clothingItemDao())
    }
    val outfitDaoRepository by lazy {
        OutfitDaoRepository(database.outfitDao())
    }
    val statisticsDaoRepository by lazy {
        StatisticsDaoRepository(database.statisticsDao())
    }

    lateinit var languageManager: LanguageManager

    override fun onCreate() {
        super.onCreate()
        languageManager = LanguageManager(this)
        languageManager.applyProperConfiguration()
    }

    override fun attachBaseContext(base: Context) {
        languageManager = LanguageManager(base)
        val languageCode = languageManager.getCurrentLanguage()

        super.attachBaseContext(
            if (languageCode == LanguageManager.SYSTEM_DEFAULT_LANGUAGE) {
                base
            } else {
                wrapContext(base, languageCode)
            }
        )
    }

    private fun wrapContext(context: Context, languageCode: String): Context {
        val locale = Locale.forLanguageTag(languageCode)
        val config = context.resources.configuration
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}