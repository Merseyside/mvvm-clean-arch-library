package com.upstream.basemvvmimpl.presentation.model

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.*

abstract class BaseViewModel protected constructor(private var bundle: Bundle?) : ViewModel() {

    private val TAG = "BaseViewModel"

    val inProgress = ObservableBoolean(false)
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()
    val errorMessageLiveData: MutableLiveData<TextMessage> = MutableLiveData()
    val messageLiveData: MutableLiveData<TextMessage> = MutableLiveData()
    val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val clearAll: MutableLiveData<Boolean> = MutableLiveData()

    inner class TextMessage {
        var msg: String = "message"
        var actionMsg: String = "Action"
        var listener: View.OnClickListener? = null
    }

    open fun handleError(throwable: Throwable) {
        errorLiveData.value = throwable
    }

    protected fun showMsg(msg: String) {
        val textMessage = TextMessage()
        textMessage.msg = msg
        messageLiveData.value = textMessage
    }

    protected fun showErrorMsg(msg: String) {
        val textMessage = TextMessage()
        textMessage.msg = msg
        errorMessageLiveData.value = textMessage
    }

    protected fun showMsg(msg: String, actionMsg: String, listener: View.OnClickListener?) {
        val textMessage = TextMessage()
        textMessage.msg = msg
        textMessage.actionMsg = actionMsg
        textMessage.listener = listener
        messageLiveData.value = textMessage
    }

    protected fun showErrorMsg(msg: String, actionMsg: String, listener: View.OnClickListener) {
        val textMessage = TextMessage()
        textMessage.msg = msg
        textMessage.actionMsg = actionMsg
        textMessage.listener = listener
        errorMessageLiveData.value = textMessage
    }

    protected fun clearUi() {
        clearAll.value = true
    }

    open fun onError(throwable: Throwable) {}

    @CallSuper
    fun showProgress() {
        inProgress.set(true)
        isLoadingLiveData.value = true
    }

    @CallSuper
    fun hideProgress() {
        inProgress.set(false)
        isLoadingLiveData.value = false
    }

    protected abstract fun dispose()

    protected abstract fun clearDisposables()

    abstract fun updateLanguage(context: Context)

    open fun onBackPressed() : Boolean {
        return true
    }

}
