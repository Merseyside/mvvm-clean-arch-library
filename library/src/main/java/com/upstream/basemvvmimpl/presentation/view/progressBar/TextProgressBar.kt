package com.upstream.basemvvmimpl.presentation.view.progressBar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.upstream.basemvvmimpl.R

class TextProgressBar(context: Context, attributeSet: AttributeSet): LinearLayout(context, attributeSet) {

    private lateinit var progressBar: ProgressBar
    private lateinit var text: TextView

    private var textValue: String? = null

    init {
        loadAttrs(attributeSet)
        doLayout()
    }

    private fun loadAttrs(attributeSet: AttributeSet) {
        val array = context.theme.obtainStyledAttributes(attributeSet, R.styleable.ProgressBarAttr, 0, 0)

        textValue = array.getString(R.styleable.ProgressBarAttr_text) ?: ""
    }

    private fun doLayout() {
        LayoutInflater.from(context).inflate(R.layout.view_progress_bar, this)

        progressBar = findViewById(R.id.text_progress)
        text = findViewById(R.id.text)

        setText(textValue)
    }

    fun setText(value: String?) {
        textValue = value

        text.text = value

        if (value.isNullOrEmpty()) {
            text.visibility = View.GONE
        } else {
            text.visibility = View.VISIBLE
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)

        text.visibility = visibility
        progressBar.visibility = visibility
    }


}