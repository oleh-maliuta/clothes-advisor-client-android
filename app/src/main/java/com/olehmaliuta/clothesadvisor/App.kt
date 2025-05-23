package com.olehmaliuta.clothesadvisor

import android.app.Application
import com.olehmaliuta.clothesadvisor.data.database.AppDb
import com.olehmaliuta.clothesadvisor.data.database.repositories.ClothingItemDaoRepository
import com.olehmaliuta.clothesadvisor.data.database.repositories.OutfitDaoRepository
import com.olehmaliuta.clothesadvisor.data.database.repositories.StatisticsDaoRepository
import com.olehmaliuta.clothesadvisor.ui.LanguageManager

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
    val languageManager by lazy {
        LanguageManager()
    }
}