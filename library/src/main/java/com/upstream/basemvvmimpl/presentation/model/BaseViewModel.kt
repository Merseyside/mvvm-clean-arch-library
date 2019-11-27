package com.upstream.basemvvmimpl.presentation.model

import android.content.Context
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.upstream.basemvvmimpl.BaseApplication

abstract class BaseViewModel protected constructor() : ViewModel() {

    val inProgress = ObservableBoolean(false)
    val progressText = ObservableField<String>()

    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()
    val messageLiveData: MutableLiveData<TextMessage> = MutableLiveData()
    val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val clearAll: MutableLiveData<Boolean> = MutableLiveData()
    val alertDialogLiveData: MutableLiveData<AlertDialogModel> = MutableLiveData()

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
        errorLiveData.value = throwable
    }

    protected fun showMsg(msg: String) {
        val textMessage = TextMessage(
            isError = false,
            msg = msg
        )

        messageLiveData.value = textMessage
    }

    protected fun showErrorMsg(msg: String) {
        val textMessage = TextMessage(
            isError = true,
            msg = msg
        )

        messageLiveData.value = textMessage
    }

    protected fun showMsg(msg: String, actionMsg: String, listener: View.OnClickListener?) {
        val textMessage = TextMessage(
            isError = false,
            msg = msg,
            actionMsg = actionMsg
        )

        messageLiveData.value = textMessage
    }

    protected fun showErrorMsg(msg: String, actionMsg: String, listener: View.OnClickListener) {
        val textMessage = TextMessage(
            isError = true,
            msg = msg,
            actionMsg = actionMsg,
            listener = listener
        )

        messageLiveData.value = textMessage
    }

    protected fun clearUi() {
        clearAll.value = true
    }

    open fun onError(throwable: Throwable) {}

    @CallSuper
    fun showProgress(text: String? = null) {
        inProgress.set(true)
        progressText.set(text)

        isLoadingLiveData.value = true
    }

    @CallSuper
    fun hideProgress() {
        if (isLoadingLiveData.value == true) {
            inProgress.set(false)
            progressText.set(null)

            isLoadingLiveData.value = false
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
        alertDialogLiveData.value = AlertDialogModel(
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
            getActualString(context, titleRes),
            getActualString(context, messageRes),
            getActualString(context, positiveButtonTextRes),
            getActualString(context, negativeButtonTextRes),
            onPositiveClick,
            onNegativeClick,
            isCancelable
        )
    }

    fun getActualString(context: Context, @StringRes id: Int?, vararg args: String): String? {
        return if (id != null) {
            (context as BaseApplication).getActualString(id, *args)
        } else {
            null
        }
    }

    protected abstract fun dispose()

    abstract fun updateLanguage(context: Context)

    open fun onBackPressed() : Boolean {
        return true
    }

    override fun onCleared() {
        super.onCleared()
        dispose()
    }
}
