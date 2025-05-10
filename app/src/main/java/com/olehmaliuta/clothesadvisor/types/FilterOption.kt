package com.olehmaliuta.clothesadvisor.types

data class FilterOption(
    val value: String,
    var displayName: String,
    var isSelected: Boolean = false
)