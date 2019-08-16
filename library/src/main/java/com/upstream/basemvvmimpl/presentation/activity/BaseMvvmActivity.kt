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

    private val messageObserver = Observer<BaseViewModel.TextMessage> { message ->
        if (message.isError) {
            showErrorMsg(message!!)
        } else {
            showMsg(message)
        }

        viewModel.messageLiveData.value = null
    }

    private val loadingObserver = Observer<Boolean> { this.loadingObserver(it!!) }
    private val clearObserver = Observer<Boolean>{ clear() }

    abstract fun setBindingVariable(): Int

    @LayoutRes
    abstract fun setLayoutId(): Int

    protected abstract fun performInjection(bundle: Bundle?)

    override fun onCreate(savedInstance: Bundle?) {
        performInjection(savedInstance)
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

    protected fun getCurrentFragment(res: Int? = getFragmentContainer()): BaseFragment? {

        res?.let {
            if (supportFragmentManager.findFragmentById(res) is BaseFragment) {
                return supportFragmentManager
                    .findFragmentById(res) as BaseFragment
            }
        }

        return null
    }
}
