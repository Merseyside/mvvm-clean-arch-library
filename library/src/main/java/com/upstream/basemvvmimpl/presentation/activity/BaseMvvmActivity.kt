package com.upstream.basemvvmimpl.presentation.activity

import android.content.Context

import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.upstream.basemvvmimpl.presentation.model.BaseViewModel
import javax.inject.Inject

abstract class BaseMvvmActivity<B : ViewDataBinding, M : BaseViewModel> : BaseActivity() {

    protected lateinit var viewDataBinding: B

    @Inject
    protected lateinit var viewModel: M

    private val errorObserver = Observer<BaseViewModel.TextMessage> { this.showErrorMsg(it!!) }
    private val messageObserver = Observer<BaseViewModel.TextMessage> { this.showMsg(it!!) }
    private val loadingObserver = Observer<Boolean> { this.loadingObserver(it!!) }
    private val clearObserver = Observer<Boolean>{ clear() }

    abstract fun setBindingVariable(): Int

    @LayoutRes
    abstract fun setLayoutId(): Int

    protected abstract fun performInjection()

    override fun onCreate(savedInstance: Bundle?) {
        performInjection()
        performDataBinding()

        super.onCreate(savedInstance)

        viewModel.updateLanguage(this)

        observeViewModel()
    }

    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, setLayoutId())
        viewDataBinding.lifecycleOwner = this
        viewDataBinding.setVariable(setBindingVariable(), viewModel)
        viewDataBinding.executePendingBindings()
    }

    private fun observeViewModel() {

        viewModel.errorMessageLiveData.observe(this, errorObserver)
        viewModel.messageLiveData.observe(this, messageObserver)
        viewModel.isLoadingLiveData.observe(this, loadingObserver)
        viewModel.clearAll.observe(this, clearObserver)
    }

    protected abstract fun clear()

    override fun handleError(throwable: Throwable) {
        viewModel.onError(throwable)
    }

    override fun updateLanguage(context: Context) {
        viewModel.updateLanguage(context)
    }

    protected abstract fun loadingObserver(isLoading: Boolean)


    protected fun showErrorMsg(textMessage: BaseViewModel.TextMessage) {
        if (TextUtils.isEmpty(textMessage.actionMsg)) {
            showErrorMsg(textMessage.msg!!)
        } else {
            showErrorMsg(textMessage.msg!!, textMessage.actionMsg!!, textMessage.listener!!)
        }
    }

    protected fun showMsg(textMessage: BaseViewModel.TextMessage) {
        if (TextUtils.isEmpty(textMessage.actionMsg)) {
            showMsg(textMessage.msg!!)
        } else {
            showMsg(textMessage.msg!!, textMessage.actionMsg!!, textMessage.listener!!)
        }
    }
}
