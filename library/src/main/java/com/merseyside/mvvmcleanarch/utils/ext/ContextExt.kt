package com.merseyside.mvvmcleanarch.utils.ext

import android.content.Context
import android.util.Log
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.merseyside.mvvmcleanarch.BaseApplication

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)

    return typedValue.data
}

fun Context.getActualString(@StringRes id: Int?, vararg args: String): String? {
    return if (id != null) {
        if (this.applicationContext is BaseApplication) {
            (this.applicationContext as BaseApplication).getActualString(id, *args)
        } else {
            throw IllegalArgumentException("Your app class must be extended by BaseApplication. Your class is ${this.javaClass}")
        }
    } else {
        null
    }
}

private const val TAG = "ContextExt"
