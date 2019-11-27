package com.upstream.basemvvmimpl.presentation.view

import androidx.databinding.BindingAdapter
import android.view.View

@BindingAdapter("app:isVisibleOrGone")
fun isVisibleOrGone(view: View, isVisible: Boolean) {
    when(isVisible) {
        true -> view.visibility = View.VISIBLE
        false -> view.visibility = View.GONE
    }

    view.invalidate()
}

@BindingAdapter("app:isVisible")
fun isVisible(view: View, isVisible: Boolean) {
    when(isVisible) {
        true -> view.visibility = View.VISIBLE
        false -> view.visibility = View.INVISIBLE
    }
}

