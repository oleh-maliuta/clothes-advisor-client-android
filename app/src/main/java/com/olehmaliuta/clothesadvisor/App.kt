package com.olehmaliuta.clothesadvisor

import android.app.Application
import com.olehmaliuta.clothesadvisor.database.AppDb
import com.olehmaliuta.clothesadvisor.database.repositories.ClothingItemDaoRepository
import com.olehmaliuta.clothesadvisor.database.repositories.OutfitDaoRepository

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
}