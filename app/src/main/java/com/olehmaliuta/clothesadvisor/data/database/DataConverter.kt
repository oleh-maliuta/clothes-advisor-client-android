package com.olehmaliuta.clothesadvisor.data.database

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
    fun fromJsonToLongList(value: String?): List<Long> {
        if (value.isNullOrEmpty() || value == "null") {
            return emptyList()
        }
        return try {
            if (value.startsWith('[')) {
                val type = object : TypeToken<List<Long>>() {}.type
                gson.fromJson(value, type)
            } else {
                value.split(",").map { it.trim().toLong() }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromLongListToJson(list: List<Long>?): String {
        return list?.let { gson.toJson(it) } ?: "[]"
    }
}