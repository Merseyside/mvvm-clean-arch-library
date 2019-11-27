package com.upstream.basemvvmimpl.utils

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.SortedList
import com.upstream.basemvvmimpl.presentation.model.BaseComparableAdapterViewModel

@Throws(IllegalArgumentException::class)
fun <T : BaseComparableAdapterViewModel<M>, M : Any> SortedList<T>.isContentEquals(list : MutableList<T>) : Boolean {

    if (this.size() != list.size) {
        return false
    } else {

        var isEquals = true
        list.forEachIndexed { index, t ->
            val value = this.get(index)
            if (!value.areItemsTheSame(t.getItem())) {
                isEquals = false
                return@forEachIndexed
            }
        }

        return isEquals
    }

}

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)

    return typedValue.data
}

fun Int.isZero(): Boolean {
    return this == 0
}

fun Double.isZero(): Boolean {
    return this == 0.0
}

fun Float.isZero(): Boolean {
    return this == 0f
}

fun Short.isZero(): Boolean {
    return this.toInt() == 0
}

fun Int.isNotZero(): Boolean {
    return this != 0
}

fun Double.isNotZero(): Boolean {
    return this != 0.0
}

fun Float.isNotZero(): Boolean {
    return this != 0f
}

fun Short.isNotZero(): Boolean {
    return this.toInt() != 0
}