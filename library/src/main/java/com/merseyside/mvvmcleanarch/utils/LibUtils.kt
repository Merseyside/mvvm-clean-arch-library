@file:JvmName("LibUtils")
package com.merseyside.mvvmcleanarch.utils

import android.content.Context
import android.util.Log
import java.util.*

internal fun getLocalizedContext(localeManager: LocaleManager): Context {
    return if (localeManager.language.isNotEmpty()) {
        localeManager.setLocale()
    } else {
        localeManager.context
    }
}

fun randomTrueOrFalse(positiveProbability: Float): Boolean {
    return when {
        positiveProbability >= 1f -> true
        positiveProbability <= 0f -> false

        else -> {
            val rand = Random()
            rand.nextFloat() <= positiveProbability
        }
    }
}

fun convertPixelsToDp(context: Context, px: Int): Float {
    val density = context.resources.displayMetrics.density
    return px / density

}

fun convertDpToPixel(context: Context, dp: Float): Float {
    val density = context.resources.displayMetrics.density
    return dp / density
}

private const val TAG = "CleanUtils"