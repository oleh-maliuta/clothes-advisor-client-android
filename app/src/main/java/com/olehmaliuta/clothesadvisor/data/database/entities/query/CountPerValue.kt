package com.olehmaliuta.clothesadvisor.data.database.entities.query

import androidx.room.ColumnInfo

data class CountPerValue(
    @ColumnInfo(name = "value")
    var value: String,
    @ColumnInfo(name = "count")
    var count: Long,
)