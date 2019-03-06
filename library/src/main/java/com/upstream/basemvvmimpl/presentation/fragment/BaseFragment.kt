package com.upstream.basemvvmimpl.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.upstream.basemvvmimpl.presentation.activity.BaseActivity
import com.upstream.basemvvmimpl.presentation.view.IView
import com.upstream.basemvvmimpl.presentation.view.OnBackPressedListener

abstract class BaseFragment : Fragment(), IView, OnBackPressedListener {

    private val TAG = "BaseFragment"
    private lateinit var context: Context

    lateinit var baseActivityView: BaseActivity
        private set

    protected var bundle: Bundle? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
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

    private fun setTitle(context: Context = getApplicationContext()) {
        val title = getTitle(context)
        if (!TextUtils.isEmpty(title) && baseActivityView.supportActionBar != null) {
            baseActivityView.supportActionBar!!.title = title
        }
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        updateLanguage()
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    abstract fun getApplicationContext() : Context
}
