package com.olehmaliuta.clothesadvisor.data.database.entities

import androidx.annotation.Size
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "clothing_items")
data class ClothingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "filename")
    val filename: String,

    @ColumnInfo(name = "name")
    @Size(max = 100)
    val name: String,

    @ColumnInfo(name = "category")
    @Size(max = 50)
    val category: String,

    @ColumnInfo(name = "season")
    @Size(max = 20)
    val season: String,

    @ColumnInfo(name = "red")
    val red: Int,

    @ColumnInfo(name = "green")
    val green: Int,

    @ColumnInfo(name = "blue")
    val blue: Int,

    @ColumnInfo(name = "material")
    @Size(max = 50)
    val material: String,

    @ColumnInfo(
        name = "brand",
        defaultValue = "NULL")
    @Size(max = 100)
    val brand: String? = null,

    @SerializedName("purchase_date")
    @ColumnInfo(
        name = "purchase_date",
        defaultValue = "NULL")
    val purchaseDate: String? = null,

    @ColumnInfo(
        name = "price",
        defaultValue = "NULL")
    val price: Double? = null,

    @SerializedName("is_favorite")
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,
)