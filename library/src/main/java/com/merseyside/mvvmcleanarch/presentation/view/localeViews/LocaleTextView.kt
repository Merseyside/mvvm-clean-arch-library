package com.merseyside.mvvmcleanarch.presentation.view.localeViews

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import com.merseyside.mvvmcleanarch.R

class LocaleTextView(
    context: Context,
    attributeSet: AttributeSet
) : AppCompatTextView(context, attributeSet), ILocaleTextView {

    @StringRes
    override var textId: Int? = null
    override var args: Array<String> = emptyArray()

    init {
        loadAttrs(attributeSet)
    }

    private fun loadAttrs(attributeSet: AttributeSet) {
        val array = context.theme.obtainStyledAttributes(attributeSet, R.styleable.LocaleTextView, 0, 0)

        textId = array.getResourceId(R.styleable.LocaleTextView_android_text, 0)
    }

    override fun getView(): TextView {
        return this
    }

    override fun getLocaleContext(): Context {
        return context
    }

}