package com.olehmaliuta.clothesadvisor.utils

import kotlin.math.roundToInt

fun Float.roundToDecimals(decimals: Int): Float {
    var multiplier = 1.0f
    repeat(decimals) { multiplier *= 10 }
    return (this * multiplier).roundToInt() / multiplier
}