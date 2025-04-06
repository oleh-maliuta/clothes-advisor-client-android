package com.olehmaliuta.clothesadvisor.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.olehmaliuta.clothesadvisor.database.access.ClothDao
import com.olehmaliuta.clothesadvisor.database.entities.*

@Database(
    entities = [
        Cloth::class,
        Outfit::class
    ],
    version = 1
)
abstract class AppDb : RoomDatabase() {
    abstract val clothDao: ClothDao

    companion object {
        fun createDb(context: Context): AppDb {
            return Room.databaseBuilder(
                context,
                AppDb::class.java,
                "main.db"
            ).build()
        }
    }
}