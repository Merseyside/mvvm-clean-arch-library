package com.merseyside.mvvmcleanarch.presentation.model

import android.content.Context
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.merseyside.mvvmcleanarch.utils.SingleLiveEvent
import com.merseyside.mvvmcleanarch.utils.ext.getActualString

abstract class BaseViewModel protected constructor() : ViewModel() {

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
        val isCancelable: Boolean = true
    )

    open fun handleError(throwable: Throwable) {
        errorLiveEvent.value = throwable
    }

    protected fun showMsg(msg: String) {
        val textMessage = TextMessage(
            isError = false,
            msg = msg
        )

        messageLiveEvent.value = textMessage
    }

    protected fun showErrorMsg(msg: String) {
        val textMessage = TextMessage(
            isError = true,
            msg = msg
        )

        messageLiveEvent.value = textMessage
    }

    protected fun showMsg(msg: String, actionMsg: String, listener: View.OnClickListener?) {
        val textMessage = TextMessage(
            isError = false,
            msg = msg,
            actionMsg = actionMsg
        )

        messageLiveEvent.value = textMessage
    }

    protected fun showErrorMsg(msg: String, actionMsg: String, listener: View.OnClickListener) {
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
        isCancelable: Boolean = true
    ) {
        alertDialogLiveEvent.value = AlertDialogModel(
            title, message, positiveButtonText, negativeButtonText, onPositiveClick, onNegativeClick, isCancelable
        )
    }

    fun showAlertDialog(
        context: Context,
        @StringRes titleRes: Int? = null,
        @StringRes messageRes: Int? = null,
        @StringRes positiveButtonTextRes: Int? = null,
        @StringRes negativeButtonTextRes: Int? = null,
        onPositiveClick: () -> Unit = {},
        onNegativeClick: () -> Unit = {},
        isCancelable: Boolean = true
    ) {

        showAlertDialog(
            getString(context, titleRes),
            getString(context, messageRes),
            getString(context, positiveButtonTextRes),
            getString(context, negativeButtonTextRes),
            onPositiveClick,
            onNegativeClick,
            isCancelable
        )
    }

    fun getString(context: Context, @StringRes id: Int?, vararg args: String): String? {
        return context.getActualString(id, *args)
    }

    fun getString(context: Context, @StringRes id: Int, vararg args: String): String {
        return context.getActualString(id, *args)!!
    }

    protected abstract fun dispose()

    fun stopAllWorks() {
        dispose()
    }

    abstract fun updateLanguage(context: Context)

    open fun onBackPressed() : Boolean {
        return true
    }

    override fun onCleared() {
        super.onCleared()
        dispose()
    }
}
