package com.upstream.basemvvmimpl.presentation.view

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button

interface IFocusManager {

    fun setOnFocusListener(root: View? = null, listener: View.OnTouchListener) {

        val view = root ?: getRootView()

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setOnFocusListener(view.getChildAt(i), listener)
            }
        } else {
            view.setOnTouchListener(listener)
        }
    }

    fun clearFocus(vararg excludeViews: View) {

        fun clearFocus(view: View) {
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    clearFocus(view.getChildAt(i))
                }
            } else {
                if (!excludeViews.contains(view) || view is Button) {
                    view.clearFocus()
                }
            }
        }

        clearFocus(getRootView())
    }

    fun keepOneFocusedView() {
        setOnFocusListener(listener = View.OnTouchListener { v, event ->
            when(event.action) {
                MotionEvent.ACTION_UP -> {
                    clearFocus(v)
                }
            }

            false
        })
    }

    fun getRootView(): View

    companion object {
        private const val TAG = "IFocusManager"
    }
}