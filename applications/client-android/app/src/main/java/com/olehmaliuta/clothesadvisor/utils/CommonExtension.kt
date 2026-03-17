package com.olehmaliuta.clothesadvisor.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import kotlin.math.roundToInt

fun Float.roundToDecimals(decimals: Int): Float {
    if (decimals < 0) {
        throw IllegalArgumentException("Decimals count must be a positive number!")
    }

    var multiplier = 1.0f
    repeat(decimals) { multiplier *= 10 }
    return (this * multiplier).roundToInt() / multiplier
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}