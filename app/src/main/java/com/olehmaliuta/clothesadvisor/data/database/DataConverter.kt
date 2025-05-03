package com.olehmaliuta.clothesadvisor.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class DataConverter {
    @TypeConverter
    fun fromDateToString(date: Date?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromStringToDate(dateString: String): Date? {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            format.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @TypeConverter
    fun fromStringToFloat(string: String): Float {
        return string.toFloat()
    }
}