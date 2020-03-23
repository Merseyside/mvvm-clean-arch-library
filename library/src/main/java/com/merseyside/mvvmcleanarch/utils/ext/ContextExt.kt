package com.merseyside.mvvmcleanarch.utils.ext

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.merseyside.mvvmcleanarch.BaseApplication
import com.merseyside.mvvmcleanarch.utils.Logger

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)

    return typedValue.data
}

fun Context.getActualString(@StringRes id: Int?, vararg args: String?): String? {
    return if (id != null) {
        val formattedArgs = args.map {
            it ?: ""
        }.toTypedArray()

        if (this.applicationContext is BaseApplication) {
            return try {
                (this.applicationContext as BaseApplication).getActualString(id, *formattedArgs)
            } catch (e: Exception) {
                Logger.logErr("ContextExt", "Resource not found! [$id]")
                return null
            }
        } else {
            this.getString(id, *args)
        }
    } else {
        null
    }
}

fun getString(context: Context, @StringRes id: Int, vararg args: String): String {
    return context.getActualString(id, *args)!!
}

fun getString(context: Context, @StringRes id: Int?, vararg args: String?): String? {
    return context.getActualString(id, *args)
}



private const val TAG = "ContextExt"
