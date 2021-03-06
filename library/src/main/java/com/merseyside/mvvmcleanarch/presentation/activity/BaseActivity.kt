package com.merseyside.mvvmcleanarch.presentation.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.merseyside.mvvmcleanarch.BaseApplication
import com.merseyside.mvvmcleanarch.presentation.dialog.MaterialAlertDialog
import com.merseyside.mvvmcleanarch.presentation.fragment.BaseFragment
import com.merseyside.mvvmcleanarch.presentation.view.OnBackPressedListener
import com.merseyside.mvvmcleanarch.presentation.view.OnKeyboardStateListener
import com.merseyside.mvvmcleanarch.presentation.view.OrientationHandler
import com.merseyside.mvvmcleanarch.presentation.view.localeViews.ILocaleManager
import com.merseyside.mvvmcleanarch.utils.LocaleManager
import com.merseyside.mvvmcleanarch.utils.Logger
import com.merseyside.mvvmcleanarch.utils.SnackbarManager
import com.merseyside.mvvmcleanarch.utils.ext.getActualString
import com.merseyside.mvvmcleanarch.utils.getLocalizedContext
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar

abstract class BaseActivity : AppCompatActivity(),
    IActivityView, OrientationHandler, ILocaleManager {

    override var keyboardUnregistrar: Any? = null

    final override var orientation: Orientation? = null

    private var application: BaseApplication? = null
    private lateinit var mainContext: Context

    override fun getContext(): Context {
        return mainContext
    }

    lateinit var snackbarManager: SnackbarManager

    override fun onOrientationChanged(orientation: Orientation, savedInstanceState: Bundle?) {}

    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) {
            val localeManager = LocaleManager(newBase)

            super.attachBaseContext(getLocalizedContext(localeManager).also { mainContext = it })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (applicationContext is BaseApplication) {
            application = applicationContext as BaseApplication
        }

        setOrientation(resources, savedInstanceState)

        if (this !is BaseMvvmActivity<*, *>) {
            setContentView(getLayoutId())
        }

        getToolbar()?.let {
            setSupportActionBar(it)
        }

        snackbarManager = SnackbarManager(this)
    }

    override fun onStart() {
        super.onStart()

        if (this is OnKeyboardStateListener) {
            keyboardUnregistrar = registerKeyboardListener(this)
        }
    }

    override fun onStop() {
        super.onStop()

        if (snackbarManager.isShowing()) {
            snackbarManager.dismiss()
        }

        unregisterKeyboardListener()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        saveOrientation(outState)
    }

    open fun updateLanguage(context: Context) {}

    override fun setLanguage(lang: String?) {
        if (application != null) {

            val language = lang ?: getLanguage()

            mainContext = application!!.setLanguage(language)

            getCurrentFragment()?.updateLanguage(mainContext)
            updateLanguage(mainContext).also { updateLocale(context = mainContext) }
        }
    }

    override fun getLanguage(): String {
        return application?.getLanguage() ?: throw IllegalStateException("Please, extend your application from BaseApplication class")
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun showMsg(msg: String, actionMsg: String?, clickListener: View.OnClickListener?) {
        snackbarManager.apply {
            showSnackbar(
                message = msg,
                actionMsg = actionMsg,
                clickListener = clickListener
            )
        }
    }

    override fun showErrorMsg(msg: String, actionMsg: String?, clickListener: View.OnClickListener?) {
        snackbarManager.apply {
            showErrorSnackbar(
                message = msg,
                actionMsg = actionMsg,
                clickListener = clickListener
            )
        }
    }

    override fun dismissMsg() {
        if (snackbarManager.isShowing()) {
            snackbarManager.dismiss()
        } else {
            Logger.log(this, "Snackbar had not shown")
        }
    }

    override fun hideKeyboard(context: Context?, view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
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
        isOneAction: Boolean?,
        isCancelable: Boolean?
    ) {
        val dialog = MaterialAlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButtonText(positiveButtonText)
            .setNegativeButtonText(negativeButtonText)
            .setOnPositiveClick(onPositiveClick)
            .setOnNegativeClick(onNegativeClick)
            .isOneAction(isOneAction)
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
        isOneAction: Boolean?,
        isCancelable: Boolean?
    ) {

        showAlertDialog(
            getActualString(titleRes),
            getActualString(messageRes),
            getActualString(positiveButtonTextRes),
            getActualString(negativeButtonTextRes),
            onPositiveClick,
            onNegativeClick,
            isOneAction,
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

    override fun setFragmentResult(fragmentResult: BaseFragment.FragmentResult) {
        fragmentResult.let {
            getCurrentFragment()?.onFragmentResult(it.resultCode, it.requestCode, it.bundle)
        }
    }

    override fun registerKeyboardListener(listener: OnKeyboardStateListener): Unregistrar {
        return KeyboardVisibilityEvent.registerEventListener(
            this
        ) { isVisible ->
            if (isVisible) listener.onKeyboardShown()
            else listener.onKeyboardHid()
        }
    }

    override fun getRootView(): View? {
        return findViewById<View>(android.R.id.content).rootView
    }
}
