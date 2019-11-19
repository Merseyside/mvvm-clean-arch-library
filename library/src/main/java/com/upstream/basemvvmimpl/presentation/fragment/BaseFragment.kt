package com.upstream.basemvvmimpl.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.upstream.basemvvmimpl.presentation.activity.BaseActivity
import com.upstream.basemvvmimpl.presentation.view.IView

abstract class BaseFragment : Fragment(), IView {

    protected lateinit var baseActivityView: BaseActivity
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            baseActivityView = context
        }
    }

    override fun getContext(): Context {
        return baseActivityView
    }

    fun getLanguage(): String {
        return baseActivityView.getLanguage()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(setLayoutId(), container, false)
    }

    @LayoutRes
    abstract fun setLayoutId(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getToolbar()?.let {
            baseActivityView.setFragmentToolbar(it)
        }

        updateLanguage(context)
    }

    override fun onStart() {
        super.onStart()

        setTitle()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        getToolbar()?.let {
            baseActivityView.setFragmentToolbar(null)
        }
    }

    fun hideKeyboard() {
        baseActivityView.hideKeyboard()
    }

    override fun handleError(throwable: Throwable) {
        baseActivityView.handleError(throwable)
    }

    override fun showMsg(msg: String, actionMsg: String?, clickListener: View.OnClickListener?) {
        baseActivityView.showMsg(msg, actionMsg, clickListener)
    }

    override fun showErrorMsg(msg: String, actionMsg: String?, clickListener: View.OnClickListener?) {
        baseActivityView.showErrorMsg(msg, actionMsg, clickListener)
    }

    abstract fun updateLanguage(context: Context)

    override fun updateLanguage(lang: String?) {
        baseActivityView.updateLanguage(lang)

        setTitle()
    }

    protected abstract fun getTitle(context: Context): String?

    fun setTitle(title: String? = getTitle(baseActivityView)) {
        if (!TextUtils.isEmpty(title) && getActionBar() != null) {

            getActionBar()!!.title = title
        }
    }

    protected open fun getActionBar(): ActionBar? {
        return baseActivityView.supportActionBar
    }

    override fun showAlertDialog(
        title: String?,
        message: String?,
        positiveButtonText: String?,
        negativeButtonText: String?,
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit,
        isCancelable: Boolean) {
        
        baseActivityView.showAlertDialog(title, message, positiveButtonText, negativeButtonText, onPositiveClick, onNegativeClick, isCancelable)        
    }

    abstract fun getToolbar(): Toolbar?

    @CallSuper
    override fun onResume() {
        super.onResume()
        updateLanguage()
    }
}
