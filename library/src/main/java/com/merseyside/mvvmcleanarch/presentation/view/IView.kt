package com.merseyside.mvvmcleanarch.presentation.view

import android.view.View
import androidx.annotation.StringRes
import com.merseyside.mvvmcleanarch.utils.SnackbarManager

interface IView {

    var snackbarManager: SnackbarManager

    fun showMsg(msg: String, actionMsg: String? = null, clickListener: View.OnClickListener? = null)

    fun showErrorMsg(msg: String, actionMsg: String? = null, clickListener: View.OnClickListener? = null)

    fun dismissMsg()

    fun handleError(throwable: Throwable)

    fun setLanguage(lang: String? = null)
    
    fun showAlertDialog(
        title: String? = null,
        message: String? = null,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        onPositiveClick: () -> Unit = {},
        onNegativeClick: () -> Unit = {},
        isOneAction: Boolean? = null,
        isCancelable: Boolean? = null
    )

    fun showAlertDialog(
        @StringRes titleRes: Int? = null,
        @StringRes messageRes: Int? = null,
        @StringRes positiveButtonTextRes: Int? = null,
        @StringRes negativeButtonTextRes: Int? = null,
        onPositiveClick: () -> Unit = {},
        onNegativeClick: () -> Unit = {},
        isOneAction: Boolean? = null,
        isCancelable: Boolean? = null
    )

    fun getActualString(@StringRes id: Int?, vararg args: String): String?
}