package com.merseyside.mvvmcleanarch.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.merseyside.mvvmcleanarch.BaseApplication
import com.merseyside.mvvmcleanarch.presentation.activity.BaseActivity
import com.merseyside.mvvmcleanarch.presentation.view.IView
import com.merseyside.mvvmcleanarch.utils.Logger
import com.merseyside.mvvmcleanarch.utils.SnackbarManager
import kotlinx.serialization.Serializable

abstract class BaseFragment : Fragment(), IView {

    protected lateinit var baseActivityView: BaseActivity
        private set

    private var requestCode: Int? = null
    private var fragmentResult: FragmentResult? = null

    override lateinit var snackbarManager: SnackbarManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            baseActivityView = context

            snackbarManager = baseActivityView.snackbarManager
        }
    }

    override fun getContext(): Context {
        return baseActivityView.context
    }

    fun getLanguage(): String {
        return baseActivityView.getLanguage()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RESULT_CODE_KEY)) {
                val resultCode = savedInstanceState.getInt(RESULT_CODE_KEY)
                this.requestCode = savedInstanceState.getInt(REQUEST_CODE_KEY)

                var bundle: Bundle? = null
                if (savedInstanceState.containsKey(RESULT_BUNDLE_KEY)) {
                    bundle = savedInstanceState.getBundle(RESULT_BUNDLE_KEY)
                }

                this.fragmentResult = FragmentResult(resultCode, requestCode!!, bundle)
            } else if (savedInstanceState.containsKey(REQUEST_CODE_KEY)) {
                this.requestCode = savedInstanceState.getInt(REQUEST_CODE_KEY)
            }
        }

        return inflater.inflate(getLayoutId(), container, false)
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getToolbar()?.let {
            baseActivityView.setFragmentToolbar(it)
        }
    }

    override fun onStart() {
        super.onStart()

        setTitle()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (fragmentResult != null) {
            outState.putInt(RESULT_CODE_KEY, fragmentResult!!.resultCode)
            outState.putInt(REQUEST_CODE_KEY, fragmentResult!!.requestCode)
            if (fragmentResult!!.bundle != null) {
                outState.putBundle(RESULT_BUNDLE_KEY, fragmentResult!!.bundle)
            }
        } else if (requestCode != null) {
            outState.putInt(REQUEST_CODE_KEY, requestCode!!)
        }
    }

    override fun onStop() {
        super.onStop()

        if (snackbarManager.isShowing()) {
            Logger.log(this, "here")
            snackbarManager.dismiss()
        }
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

    open fun updateLanguage(context: Context) {}

    override fun setLanguage(lang: String?) {
        baseActivityView.setLanguage(lang)

        setTitle()
    }

    protected abstract fun getTitle(context: Context): String?

    fun setTitle(title: String? = getTitle((baseActivityView.applicationContext as BaseApplication).context)) {
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
        isOneAction: Boolean?,
        isCancelable: Boolean?) {
        
        baseActivityView.showAlertDialog(
            title,
            message,
            positiveButtonText,
            negativeButtonText,
            onPositiveClick,
            onNegativeClick,
            isOneAction,
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
        isOneAction: Boolean?,
        isCancelable: Boolean?
    ) {

        baseActivityView.showAlertDialog(
            titleRes,
            messageRes,
            positiveButtonTextRes,
            negativeButtonTextRes,
            onPositiveClick,
            onNegativeClick,
            isOneAction,
            isCancelable
        )
    }

    override fun getActualString(@StringRes id: Int?, vararg args: String): String? {
        return baseActivityView.getActualString(id, *args)
    }

    open fun getToolbar(): Toolbar? {
        return null
    }

    override fun onDetach() {
        super.onDetach()

        if (requestCode != null) {
            val result = if (fragmentResult == null) {
                FragmentResult(RESULT_CANCELLED, requestCode!!)
            } else {
                fragmentResult!!
            }

            baseActivityView.setFragmentResult(result)
        }
    }

    protected fun setFragmentResult(resultCode: Int, bundle: Bundle? = null) {
        if (requestCode != null) {
            this.fragmentResult = FragmentResult(resultCode, requestCode!!, bundle)
        } else throw IllegalStateException("Firstly, set request code")
    }

    protected fun setRequestCode(requestCode: Int) {
        this.requestCode = requestCode
    }

    open fun onFragmentResult(resultCode: Int, requestCode: Int, bundle: Bundle? = null) {}

    class FragmentResult(
        val resultCode: Int,
        val requestCode: Int,
        val bundle: Bundle? = null
    )

    companion object {
        const val RESULT_OK = -1
        const val RESULT_CANCELLED = 0

        private const val REQUEST_CODE_KEY = "requestCode"
        private const val RESULT_CODE_KEY = "resultCode"
        private const val RESULT_BUNDLE_KEY = "resultBundle"
    }
}
