package com.olehmaliuta.clothesadvisor.utils.types

data class FilterOption(
    val value: String,
    var displayNameId: Int,
    var isSelected: Boolean = false
)