package com.merseyside.mvvmcleanarch.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.merseyside.mvvmcleanarch.BaseApplication
import com.merseyside.mvvmcleanarch.presentation.activity.BaseActivity
import com.merseyside.mvvmcleanarch.presentation.view.IView

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
        return inflater.inflate(getLayoutId(), container, false)
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

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

    override fun setLanguage(lang: String?) {
        baseActivityView.setLanguage(lang)

        setTitle()
    }

    protected abstract fun getTitle(context: Context): String?

    fun setTitle(title: String? = getTitle((baseActivityView.applicationContext as BaseApplication).getContext())) {
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
        
        baseActivityView.showAlertDialog(
            title,
            message,
            positiveButtonText,
            negativeButtonText,
            onPositiveClick,
            onNegativeClick,
            isCancelable
        )
    }

    override fun showAlertDialog(
        @StringRes titleRes: Int?,
        @StringRes messageRes: Int?,
        @StringRes positiveButtonTextRes: Int?,
        @StringRes negativeButtonTextRes: Int?,
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit,
        isCancelable: Boolean
    ) {

        baseActivityView.showAlertDialog(
            titleRes,
            messageRes,
            positiveButtonTextRes,
            negativeButtonTextRes,
            onPositiveClick,
            onNegativeClick,
            isCancelable
        )
    }

    override fun getActualString(@StringRes id: Int?, vararg args: String): String? {
        return baseActivityView.getActualString(id, *args)
    }

    abstract fun getToolbar(): Toolbar?

    @CallSuper
    override fun onResume() {
        super.onResume()
        updateLanguage((baseActivityView.applicationContext as BaseApplication).getContext())
    }
}
