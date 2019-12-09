package com.merseyside.mvvmcleanarch.presentation.activity

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.merseyside.mvvmcleanarch.BaseApplication
import com.merseyside.mvvmcleanarch.R
import com.merseyside.mvvmcleanarch.presentation.dialog.MaterialAlertDialog
import com.merseyside.mvvmcleanarch.presentation.fragment.BaseFragment
import com.merseyside.mvvmcleanarch.utils.ext.getColorFromAttr
import com.merseyside.mvvmcleanarch.presentation.view.IActivityView
import com.merseyside.mvvmcleanarch.presentation.view.OnBackPressedListener
import com.merseyside.mvvmcleanarch.utils.LocaleManager
import com.merseyside.mvvmcleanarch.utils.ext.getActualString
import com.merseyside.mvvmcleanarch.utils.getLocalizedContext
import java.lang.IllegalStateException

abstract class BaseActivity : AppCompatActivity(), IActivityView {

    private var application: BaseApplication? = null

    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) {
            val localeManager = LocaleManager(newBase)

            super.attachBaseContext(getLocalizedContext(localeManager))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (applicationContext is BaseApplication) {
            application = applicationContext as BaseApplication
        }

        if (this !is BaseMvvmActivity<*, *>) {
            setContentView(getLayoutId())
        }

        getToolbar()?.let {
            setSupportActionBar(it)
        }
    }

    open fun updateLanguage(context: Context) {}

    override fun setLanguage(lang: String?) {
        if (application != null) {

            val language = lang ?: getLanguage()

            val context = application!!.setLanguage(language)

            getCurrentFragment()?.updateLanguage(context)
            updateLanguage(context)
        }
    }

    override fun getLanguage(): String {
        return application?.getLanguage() ?: throw IllegalStateException("Please, extend your application from BaseApplication class")
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    @CallSuper
    override fun onResume() {
        super.onResume()
    }

    override fun showMsg(msg: String, actionMsg: String?, clickListener: View.OnClickListener?) {

        val length = if (!actionMsg.isNullOrEmpty()) {
            Snackbar.LENGTH_INDEFINITE
        } else {
            Snackbar.LENGTH_SHORT
        }

        showSnackbar(
            message = msg,
            length = length,
            backgroundColor = getMsgBackgroundColor(),
            textColor = getMsgTextColor(),
            actionColor = getActionMsgTextColor(),
            actionMsg = actionMsg,
            clickListener = clickListener)
    }

    override fun showErrorMsg(msg: String, actionMsg: String?, clickListener: View.OnClickListener?) {

        val length = if (!actionMsg.isNullOrEmpty()) {
            Snackbar.LENGTH_INDEFINITE
        } else {
            Snackbar.LENGTH_LONG
        }

        showSnackbar(message = msg,
            length = length,
            backgroundColor = getErrorMsgBackgroundColor(),
            textColor = getErrorMsgTextColor(),
            actionColor = getActionErrorMsgTextColor(),
            actionMsg = actionMsg,
            clickListener = clickListener
        )
    }

    fun showSnackbar(
        message: String,
        length: Int,
        @ColorInt backgroundColor: Int,
        @ColorInt textColor: Int,
        @ColorInt actionColor: Int,
        actionMsg: String?,
        clickListener: View.OnClickListener?
    ) {
        createSnackbar(message, length, backgroundColor, textColor).apply {

            if (!actionMsg.isNullOrEmpty()) {
                var listener = clickListener

                if (listener == null) listener = View.OnClickListener {}

                setAction(actionMsg, listener)
                setActionTextColor(actionColor)
            }

            show()
        }
    }

    private fun createSnackbar(message: String, length: Int, @ColorInt backgroundColor: Int, @ColorInt textColor: Int): Snackbar {
        
        val viewGroup = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        val snackbar = Snackbar.make(viewGroup, message, length)
        val snackbarView = snackbar.view

        snackbarView.setBackgroundColor(backgroundColor)

        val snackTextView = snackbarView.findViewById<TextView>(R.id.snackbar_text)
        snackTextView.setTextColor(textColor)

        val font = Typeface.createFromAsset(this.assets, "fonts/Roboto-Regular.ttf")
        var tv = snackbar.view.findViewById<TextView>(R.id.snackbar_text)
        tv.typeface = font
        tv = snackbar.view.findViewById(R.id.snackbar_action)
        tv.typeface = font

        return snackbar
    }

    override fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @ColorInt
    open fun getMsgBackgroundColor(): Int {
        return getColorFromAttr(R.attr.colorPrimaryVariant)
    }

    @ColorInt
    open fun getErrorMsgBackgroundColor(): Int {
        return getColorFromAttr(R.attr.colorError)
    }

    @ColorInt
    open fun getMsgTextColor(): Int {
        return getColorFromAttr(R.attr.colorOnPrimary)
    }

    @ColorInt
    open fun getErrorMsgTextColor(): Int {
        return getColorFromAttr(R.attr.colorOnError)
    }

    @ColorInt
    open fun getActionMsgTextColor(): Int {
        return getColorFromAttr(R.attr.colorOnPrimary)
    }

    @ColorInt
    open fun getActionErrorMsgTextColor(): Int {
        return getColorFromAttr(R.attr.colorOnError)
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

    override fun handleError(throwable: Throwable) {}

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

    override fun showAlertDialog(
        title: String?,
        message: String?,
        positiveButtonText: String?,
        negativeButtonText: String?,
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit,
        isCancelable: Boolean
    ) {
        val dialog = MaterialAlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButtonText(positiveButtonText)
            .setNegativeButtonText(negativeButtonText)
            .setOnPositiveClick(onPositiveClick)
            .setOnNegativeClick(onNegativeClick)
            .isCancelable(isCancelable)
            .build()
        
        dialog.show()
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

        showAlertDialog(
            getActualString(titleRes),
            getActualString(messageRes),
            getActualString(positiveButtonTextRes),
            getActualString(negativeButtonTextRes),
            onPositiveClick,
            onNegativeClick,
            isCancelable
        )
    }

    abstract fun getToolbar(): Toolbar?

    override fun setFragmentToolbar(toolbar: Toolbar?) {
        if (toolbar != null) {
            supportActionBar?.hide()
            setSupportActionBar(toolbar)

        } else {
            getToolbar()?.let {
                setSupportActionBar(it)
                supportActionBar?.show()
            }
        }
    }

    override fun getActualString(@StringRes id: Int?, vararg args: String): String? {
        return applicationContext.getActualString(id, *args)
    }

    companion object {
        private const val TAG = "BaseActivity"
    }
}
