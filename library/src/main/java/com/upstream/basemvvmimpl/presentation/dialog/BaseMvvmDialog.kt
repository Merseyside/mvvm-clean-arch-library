package com.upstream.basemvvmimpl.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.upstream.basemvvmimpl.presentation.model.BaseViewModel

abstract class BaseMvvmDialog<B : ViewDataBinding, M : BaseViewModel> : BaseDialog() {

    private lateinit var binding: B
    private lateinit var viewModel: M

    private val errorObserver = Observer<Throwable> { this.showError(it!!) }
    private val messageObserver = Observer<BaseViewModel.TextMessage> { this.showMsg(it!!) }

    override fun onCreate(onSavedInstanceState: Bundle?) {
        performInjection()
        super.onCreate(onSavedInstanceState)
        viewModel = setViewModel()
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, setLayoutId(), container, false)
        return binding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setVariable(setBindingVariable(), viewModel)
        binding.executePendingBindings()

        viewModel.updateLanguage(context!!)

        viewModel.errorLiveData.observe(this, errorObserver)
        viewModel.messageLiveData.observe(this, messageObserver)
    }

    abstract fun setBindingVariable(): Int

    @LayoutRes
    abstract fun setLayoutId(): Int

    abstract fun setViewModel(): M

    private fun showMsg(textMessage: BaseViewModel.TextMessage) {
        if (textMessage.actionMsg.isNullOrEmpty()) {
            showMsg(textMessage.msg)
        } else {
            showMsg(textMessage.msg, textMessage.actionMsg!!, textMessage.listener!!)
        }
    }
}
