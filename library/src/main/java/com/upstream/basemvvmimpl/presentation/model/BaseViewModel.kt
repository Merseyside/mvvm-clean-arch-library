package com.upstream.basemvvmimpl.presentation.model

import android.content.Context
import android.view.View
import androidx.annotation.CallSuper
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.*

abstract class BaseViewModel protected constructor() : ViewModel() {

    val inProgress = ObservableBoolean(false)
    val progressText = ObservableField<String>()

    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()
    val messageLiveData: MutableLiveData<TextMessage> = MutableLiveData()
    val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val clearAll: MutableLiveData<Boolean> = MutableLiveData()

    data class TextMessage(
        val isError: Boolean = false,
        var msg: String = "",
        var actionMsg: String? = null,
        var listener: View.OnClickListener? = null
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
        inProgress.set(false)
        progressText.set(null)

        isLoadingLiveData.value = false
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
