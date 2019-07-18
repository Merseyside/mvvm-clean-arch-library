package com.upstream.basemvvmimpl.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.upstream.basemvvmimpl.presentation.activity.BaseActivity
import com.upstream.basemvvmimpl.presentation.view.IView
import com.upstream.basemvvmimpl.presentation.view.OnBackPressedListener

abstract class BaseFragment : Fragment(), IView {

    private lateinit var context: Context

    lateinit var baseActivityView: BaseActivity
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
        if (context is BaseActivity) {
            baseActivityView = context
        }
    }

    override fun getContext(): Context {
        return context
    }

    override fun onStart() {
        super.onStart()

        setTitle()
    }

    fun hideKeyboard() {
        baseActivityView.hideKeyboard()
    }

    override fun showMsg(msg: String) {
        baseActivityView.showMsg(msg)
    }

    override fun handleError(throwable: Throwable) {
        baseActivityView.handleError(throwable)
    }

    override fun showErrorMsg(msg: String) {
        baseActivityView.showErrorMsg(msg)
    }

    override fun showMsg(msg: String, actionMsg: String, clickListener: View.OnClickListener?) {
        baseActivityView.showMsg(msg, actionMsg, clickListener)
    }

    override fun showErrorMsg(msg: String, actionMsg: String, clickListener: View.OnClickListener?) {
        baseActivityView.showErrorMsg(msg, actionMsg, clickListener)
    }

    protected abstract fun updateLanguage(context: Context)

    override fun updateLanguage() {
        context = getApplicationContext()
        setTitle()
        updateLanguage(context)
    }

    protected abstract fun getTitle(context: Context): String?

    fun setTitle(title: String? = getTitle(getApplicationContext())) {
        if (!TextUtils.isEmpty(title) && getActionBar() != null) {

            getActionBar()!!.title = title
        }
    }

    protected fun getActionBar(): ActionBar? {
        return baseActivityView.supportActionBar
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        updateLanguage()
    }

    abstract fun getApplicationContext() : Context
}
