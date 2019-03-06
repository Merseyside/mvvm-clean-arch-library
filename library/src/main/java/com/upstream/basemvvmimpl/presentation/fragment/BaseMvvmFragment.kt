package com.upstream.basemvvmimpl.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.upstream.basemvvmimpl.presentation.model.BaseViewModel
import javax.inject.Inject

abstract class BaseMvvmFragment<B : ViewDataBinding, M : BaseViewModel> : BaseFragment() {

    protected lateinit var viewDataBinding: B

    @Inject
    protected lateinit var viewModel: M

    private val errorMessageObserver = Observer<BaseViewModel.TextMessage> { this.showErrorMsg(it!!) }
    private val errorObserver = Observer<Throwable> { this.handleError(it!!) }
    private val messageObserver = Observer<BaseViewModel.TextMessage> { this.showMsg(it!!) }
    private val loadingObserver = Observer<Boolean> { this.loadingObserver(it!!) }
    private val clearObserver = Observer<Boolean> { clear() }

    abstract fun setBindingVariable(): Int

    @LayoutRes
    abstract fun setLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performInjection()
        setHasOptionsMenu(false)
    }

    protected abstract fun performInjection()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, setLayoutId(), container, false)
        viewDataBinding.lifecycleOwner = this
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.setVariable(setBindingVariable(), viewModel)
        viewDataBinding.executePendingBindings()

        viewModel.updateLanguage(context)
        viewModel.errorLiveData.observe(this, errorObserver)
        viewModel.errorMessageLiveData.observe(this, errorMessageObserver)
        viewModel.messageLiveData.observe(this, messageObserver)
        viewModel.isLoadingLiveData.observe(this, loadingObserver)
        viewModel.clearAll.observe(this, clearObserver)
    }

    protected abstract fun clear()

    @CallSuper
    override fun updateLanguage(context: Context) {
        viewModel.updateLanguage(context)
    }

    protected abstract fun loadingObserver(isLoading: Boolean)

    protected fun showErrorMsg(textMessage: BaseViewModel.TextMessage) {
        if (TextUtils.isEmpty(textMessage.actionMsg)) {
            showErrorMsg(textMessage.msg)
        } else {
            showErrorMsg(textMessage.msg, textMessage.actionMsg, textMessage.listener)
        }
    }

    protected fun showMsg(textMessage: BaseViewModel.TextMessage) {
        if (TextUtils.isEmpty(textMessage.actionMsg)) {
            showMsg(textMessage.msg)
        } else {
            showMsg(textMessage.msg, textMessage.actionMsg, textMessage.listener)
        }
    }

    protected fun showProgress() {
        viewModel.showProgress()
    }

    protected fun hideProgress() {
        viewModel.hideProgress()
    }

}
