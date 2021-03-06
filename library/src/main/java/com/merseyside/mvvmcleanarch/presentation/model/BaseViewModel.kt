package com.merseyside.mvvmcleanarch.presentation.model

import android.content.Context
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.merseyside.mvvmcleanarch.presentation.interfaces.IStringHelper
import com.merseyside.mvvmcleanarch.utils.Logger
import com.merseyside.mvvmcleanarch.utils.SingleLiveEvent

abstract class BaseViewModel protected constructor() : ViewModel(), IStringHelper {

    val isInProgress = ObservableBoolean(false)
    val progressText = ObservableField<String>()

    val errorLiveEvent: MutableLiveData<Throwable> = SingleLiveEvent()
    val messageLiveEvent: MutableLiveData<TextMessage> = SingleLiveEvent()
    val isInProgressLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val alertDialogLiveEvent: MutableLiveData<AlertDialogModel> = SingleLiveEvent()

    data class TextMessage(
        val isError: Boolean = false,
        var msg: String = "",
        var actionMsg: String? = null,
        var listener: View.OnClickListener? = null
    )

    data class AlertDialogModel(
        val title: String? = null,
        val message: String? = null,
        val positiveButtonText: String? = null,
        val negativeButtonText: String? = null,
        val onPositiveClick: () -> Unit = {},
        val onNegativeClick: () -> Unit = {},
        val isOneAction: Boolean? = null,
        val isCancelable: Boolean? = null
    )

    open fun handleError(throwable: Throwable) {
        errorLiveEvent.value = throwable
    }

    protected fun showMsg(@StringRes id: Int, vararg args: String) {
        showMsg(getString(id, *args))
    }

    protected fun showErrorMsg(@StringRes id: Int, vararg args: String) {
        showErrorMsg(getString(id, *args))
    }

    protected fun showMsg(msg: String) {
        Logger.log(this, msg)
        val textMessage = TextMessage(
            isError = false,
            msg = msg
        )

        messageLiveEvent.value = textMessage
    }

    protected fun showErrorMsg(msg: String) {
        Logger.logErr(this, msg)
        val textMessage = TextMessage(
            isError = true,
            msg = msg
        )

        messageLiveEvent.value = textMessage
    }

    protected fun showMsg(msg: String, actionMsg: String, listener: View.OnClickListener?) {
        Logger.log(this, msg)
        val textMessage = TextMessage(
            isError = false,
            msg = msg,
            actionMsg = actionMsg
        )

        messageLiveEvent.value = textMessage
    }

    protected fun showErrorMsg(msg: String, actionMsg: String, listener: View.OnClickListener) {
        Logger.logErr(this, msg)
        val textMessage = TextMessage(
            isError = true,
            msg = msg,
            actionMsg = actionMsg,
            listener = listener
        )

        messageLiveEvent.value = textMessage
    }

    open fun onError(throwable: Throwable) {}

    @CallSuper
    fun showProgress(text: String? = null) {
        Logger.log(this, text ?: "Empty")

        isInProgress.set(true)
        progressText.set(text)

        isInProgressLiveData.value = true
    }

    @CallSuper
    fun hideProgress() {
        if (isInProgressLiveData.value == true) {
            isInProgress.set(false)
            progressText.set(null)

            isInProgressLiveData.value = false
        }
    }

    fun showAlertDialog(
        title: String? = null,
        message: String? = null,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        onPositiveClick: () -> Unit = {},
        onNegativeClick: () -> Unit = {},
        isOneAction: Boolean? = null,
        isCancelable: Boolean? = null
    ) {
        alertDialogLiveEvent.value = AlertDialogModel(
            title, message, positiveButtonText, negativeButtonText, onPositiveClick, onNegativeClick, isOneAction, isCancelable
        )
    }

    fun showAlertDialog(
        @StringRes titleRes: Int? = null,
        @StringRes messageRes: Int? = null,
        @StringRes positiveButtonTextRes: Int? = null,
        @StringRes negativeButtonTextRes: Int? = null,
        onPositiveClick: () -> Unit = {},
        onNegativeClick: () -> Unit = {},
        isOneAction: Boolean? = null,
        isCancelable: Boolean = true
    ) {

        showAlertDialog(
            getString(titleRes),
            getString(messageRes),
            getString(positiveButtonTextRes),
            getString(negativeButtonTextRes),
            onPositiveClick,
            onNegativeClick,
            isOneAction,
            isCancelable
        )
    }

    protected abstract fun dispose()

    fun stopAllWorks() {
        dispose()
    }

    open fun updateLanguage(context: Context) {}

    open fun onBackPressed() : Boolean {
        return true
    }

    override fun onCleared() {
        super.onCleared()
        dispose()
    }
}
