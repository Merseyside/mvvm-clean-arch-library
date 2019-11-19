package com.upstream.basemvvmimpl.presentation.view

import androidx.appcompat.widget.Toolbar

interface IActivityView : IView {

    fun hideKeyboard()

    fun onBackPressed()

    fun getLanguage(): String?

    fun setFragmentToolbar(toolbar: Toolbar?)
}