package com.olehmaliuta.clothesadvisor.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.olehmaliuta.clothesadvisor.data.database.access.ClothingItemDao
import com.olehmaliuta.clothesadvisor.data.database.access.OutfitDao
import com.olehmaliuta.clothesadvisor.data.database.access.StatisticsDao
import com.olehmaliuta.clothesadvisor.data.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.data.database.entities.ClothingItemOutfitCross
import com.olehmaliuta.clothesadvisor.data.database.entities.Outfit

@Database(
    entities = [
        ClothingItem::class,
        Outfit::class,
        ClothingItemOutfitCross::class
    ],
    version = 1
)
@TypeConverters(DataConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun clothingItemDao(): ClothingItemDao
    abstract fun outfitDao(): OutfitDao
    abstract fun statisticsDao(): StatisticsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null

        fun getDatabase(context: Context): AppDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    "main"
                ).addMigrations().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}