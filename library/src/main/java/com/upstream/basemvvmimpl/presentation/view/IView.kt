package com.upstream.basemvvmimpl.presentation.view

import android.view.View
import androidx.annotation.StringRes

interface IView {

    fun showMsg(msg: String, actionMsg: String? = null, clickListener: View.OnClickListener? = null)

    fun handleError(throwable: Throwable)

    fun showErrorMsg(msg: String, actionMsg: String? = null, clickListener: View.OnClickListener? = null)

    fun updateLanguage(lang: String? = null)
    
    fun showAlertDialog(
        title: String? = null,
        message: String? = null,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        onPositiveClick: () -> Unit = {},
        onNegativeClick: () -> Unit = {},
        isCancelable: Boolean = true
    )

    fun showAlertDialog(
        @StringRes titleRes: Int? = null,
        @StringRes messageRes: Int? = null,
        @StringRes positiveButtonTextRes: Int? = null,
        @StringRes negativeButtonTextRes: Int? = null,
        onPositiveClick: () -> Unit = {},
        onNegativeClick: () -> Unit = {},
        isCancelable: Boolean = true)
}