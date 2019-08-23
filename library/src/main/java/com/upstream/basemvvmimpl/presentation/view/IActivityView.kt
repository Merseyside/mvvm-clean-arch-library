package com.upstream.basemvvmimpl.presentation.view

interface IActivityView : IView {

    fun hideKeyboard()

    fun onBackPressed()

    fun getLanguage(): String?
}