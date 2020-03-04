package com.merseyside.mvvmcleanarch.utils.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.merseyside.mvvmcleanarch.presentation.activity.BaseActivity
import com.merseyside.mvvmcleanarch.presentation.activity.Orientation

fun EditText.setTextWithCursor(text: String?) {
    if (this.text.toString() != text) {
        text?.let {
            setText(it)
            setSelection(it.length)
        }
    }
}

fun EditText.setTextWithCursor(text: CharSequence?) {
    setTextWithCursor(text.toString())
}

fun View.getActivity(): Activity {
    var context: Context = context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }

    throw IllegalStateException("View hasn't been bind to activity!")
}

fun View.getOrientation(): Orientation {
    val activity = this.getActivity()

    return if (activity is BaseActivity) {
        activity.orientation!!
    } else {
        throw IllegalStateException("Your activity has to extend BaseActivity")
    }
}

interface TextChangeListenerUnregistrar {
    fun removeTextListener()
}

internal class TextChangeListenerUnregistrarImpl(
    private val textView: TextView,
    private val textWatcher: TextWatcher
): TextChangeListenerUnregistrar {

    override fun removeTextListener() {
        textView.removeTextChangedListener(textWatcher)
    }
}

fun TextView.addTextChangeListener(callback: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit): TextChangeListenerUnregistrar {
    val textWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            callback(s, start, before, count)
        }
    }

    this.addTextChangedListener(textWatcher)

    return TextChangeListenerUnregistrarImpl(this, textWatcher)
}