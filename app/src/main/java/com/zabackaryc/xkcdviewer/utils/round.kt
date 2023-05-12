package com.zabackaryc.xkcdviewer.utils

import kotlin.math.round

// Because Kotlin should have these already.

/**
 * Round to *i* decimal places.
 */
fun Float.round(decimals: Int): Float {
    var multiplier = 1f
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

/**
 * Round to *i* decimal places.
 */
fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}
