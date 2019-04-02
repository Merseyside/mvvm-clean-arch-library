package com.upstream.basemvvmimpl.presentation.utils

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
            if (!value.isItemsTheSame(t.getItem())) {
                isEquals = false
                return@forEachIndexed
            }
        }

        return isEquals
    }

}