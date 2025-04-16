package com.olehmaliuta.clothesadvisor.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class DataConverter {
    private val gson = Gson()

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
    fun fromJsonToIntList(value: String?): List<Int> {
        if (value.isNullOrEmpty() || value == "null") {
            return emptyList()
        }
        return try {
            if (value.startsWith('[')) {
                val type = object : TypeToken<List<Int>>() {}.type
                gson.fromJson(value, type)
            } else {
                value.split(",").map { it.trim().toInt() }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromIntListToJson(list: List<Int>?): String {
        return list?.let { gson.toJson(it) } ?: "[]"
    }
}