package com.olehmaliuta.clothesadvisor.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.olehmaliuta.clothesadvisor.database.access.ClothDao
import com.olehmaliuta.clothesadvisor.database.converters.DateConverter
import com.olehmaliuta.clothesadvisor.database.entities.*

@Database(
    entities = [
        Cloth::class,
        Outfit::class,
        ClothOutfitCross::class
    ],
    version = 1
)
@TypeConverters(value = [
    DateConverter::class
])
abstract class AppDb : RoomDatabase() {
    abstract fun clothDao(): ClothDao

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
                instance
            }
        }
    }
}