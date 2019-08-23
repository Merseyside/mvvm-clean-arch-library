package com.upstream.basemvvmimpl.presentation.view

import android.view.View

interface IView {

    fun showMsg(msg: String)

    fun handleError(throwable: Throwable)

    fun showErrorMsg(msg: String)

    fun showMsg(msg: String, actionMsg: String, clickListener: View.OnClickListener?)

    fun showErrorMsg(msg: String, actionMsg: String, clickListener: View.OnClickListener?)

    fun updateLanguage(lang: String? = null)
}