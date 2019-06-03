package com.upstream.basemvvmimpl.presentation.activity

import android.content.Context

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.upstream.basemvvmimpl.presentation.fragment.BaseFragment
import com.upstream.basemvvmimpl.presentation.model.BaseViewModel
import com.upstream.basemvvmimpl.presentation.view.OnBackPressedListener
import javax.inject.Inject

abstract class BaseMvvmActivity<B : ViewDataBinding, M : BaseViewModel> : BaseActivity() {

    protected lateinit var binding: B

    @Inject
    protected lateinit var viewModel: M

    private val errorObserver = Observer<BaseViewModel.TextMessage> { this.showErrorMsg(it!!) }
    private val messageObserver = Observer<BaseViewModel.TextMessage> { this.showMsg(it!!) }
    private val loadingObserver = Observer<Boolean> { this.loadingObserver(it!!) }
    private val clearObserver = Observer<Boolean>{ clear() }

    abstract fun setBindingVariable(): Int

    @LayoutRes
    abstract fun setLayoutId(): Int

    override fun onCreate(savedInstance: Bundle?) {
        performInjection()
        performDataBinding()

        super.onCreate(savedInstance)

        viewModel.updateLanguage(this)

        observeViewModel()
    }

    private fun performDataBinding() {
        binding = DataBindingUtil.setContentView(this, setLayoutId())
        binding.lifecycleOwner = this
        binding.setVariable(setBindingVariable(), viewModel)
        binding.executePendingBindings()
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
        if (textMessage.actionMsg.isNullOrEmpty()) {
            showErrorMsg(textMessage.msg)
        } else {
            showErrorMsg(textMessage.msg, textMessage.actionMsg!!, textMessage.listener!!)
        }
    }

    protected fun showMsg(textMessage: BaseViewModel.TextMessage) {
        if (textMessage.actionMsg.isNullOrEmpty()) {
            showMsg(textMessage.msg)
        } else {
            showMsg(textMessage.msg, textMessage.actionMsg!!, textMessage.listener!!)
        }
    }

    override fun onBackPressed() {

        val fragment = getCurrentFragment()

        if (fragment != null && fragment is OnBackPressedListener) {
            if (fragment.onBackPressed()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    @IdRes
    open fun getFragmentContainer(): Int? {
        return null
    }

    private fun getCurrentFragment(res: Int? = getFragmentContainer()): BaseFragment? {

        res?.let {
            return supportFragmentManager
                    .findFragmentById(res) as BaseFragment
        }

        return null
    }
}
