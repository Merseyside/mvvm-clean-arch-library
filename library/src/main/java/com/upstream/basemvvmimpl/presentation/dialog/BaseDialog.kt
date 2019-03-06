package com.upstream.basemvvmimpl.presentation.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.upstream.basemvvmimpl.R
import com.upstream.basemvvmimpl.presentation.activity.BaseActivity

abstract class BaseDialog : DialogFragment() {

    private val TAG = "BaseDialog"
    protected var data: Bundle? = null

    protected lateinit var baseActivity: BaseActivity

    override fun onCreate(onSavedInstanceState: Bundle?) {
        super.onCreate(onSavedInstanceState)
        data = arguments
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            this.baseActivity = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val root = RelativeLayout(activity)
        root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

        val dialog = Dialog(baseActivity, R.style.ThemeOverlay_AppCompat_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.window!!.decorView.setBackgroundResource(android.R.color.transparent)

        return dialog
    }

    override fun show(fragmentManager: FragmentManager, tag: String?) {
        val transaction = fragmentManager.beginTransaction()
        val prevFragment = fragmentManager.findFragmentByTag(tag)
        if (prevFragment != null) {
            transaction.remove(prevFragment)
        }
        transaction.addToBackStack(null)
        show(transaction, tag)
    }

    fun hideKeyboard() {
        baseActivity.hideKeyboard()
    }

    fun showMsg(msg: String) {
        baseActivity.showMsg(msg)
    }

    fun showError(throwable: Throwable) {
        baseActivity.handleError(throwable)
    }

    fun showMsg(msg: String, actionMsg: String, listener: View.OnClickListener) {
        baseActivity.showMsg(msg, actionMsg, listener)
    }

    fun showErrorMsg(msg: String, actionMsg: String, listener: View.OnClickListener) {
        baseActivity.showErrorMsg(msg, actionMsg, listener)
    }
}
