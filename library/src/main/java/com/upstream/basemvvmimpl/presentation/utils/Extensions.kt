package com.upstream.basemvvmimpl.presentation.utils

import android.util.Log
import androidx.recyclerview.widget.SortedList
import com.upstream.basemvvmimpl.presentation.model.BaseComparableAdapterViewModel
import java.lang.IllegalArgumentException

@Throws(IllegalArgumentException::class)
fun <T : BaseComparableAdapterViewModel<M>, M : Any> MutableList<T>.isContentEquals(list : MutableList<T>) : Boolean {
    Log.d("Filter", "size = ${this.size} size2 = ${list.size}")

    if (!this.isEmpty() && !list.isEmpty()) {
        var isEquals = true
        forEachIndexed { index, t ->
            Log.d("Filter", "index = $index")
            if (!list[index].isItemsTheSame(t.getItem())) {
                isEquals = false
                Log.d("Filter", "isEquals = $isEquals")
                return@forEachIndexed
            }
        }

    return isEquals
    } else {
        throw IllegalArgumentException("List is empty")
    }

}

@Throws(IllegalArgumentException::class)
fun <T : BaseComparableAdapterViewModel<M>, M : Any> SortedList<T>.isContentEquals(list : MutableList<T>) : Boolean {

    if (this.size() != list.size) {
        return false
    } else {

        var isEquals = true
        list.forEachIndexed { index, t ->
            val value = this.get(index)
            if (!value.isItemsTheSame(t.getItem())) {
                isEquals = false
                return@forEachIndexed
            }
        }

        return isEquals
    }

}