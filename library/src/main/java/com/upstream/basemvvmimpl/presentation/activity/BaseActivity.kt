package com.upstream.basemvvmimpl.presentation.activity

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.upstream.basemvvmimpl.R
import com.upstream.basemvvmimpl.presentation.view.IActivityView

abstract class BaseActivity : AppCompatActivity(), IActivityView {

    private val TAG = "BaseActivity"

    abstract fun updateLanguage(context: Context)

    override fun updateLanguage() {}

    @CallSuper
    override fun onResume() {
        super.onResume()
        updateLanguage()
    }

    override fun showMsg(msg: String) {
        showSnackbar(msg, Snackbar.LENGTH_SHORT, Color.BLUE)
    }

    override fun showErrorMsg(msg: String) {
        showSnackbar(msg, Snackbar.LENGTH_LONG, Color.RED)
    }

    override fun showMsg(msg: String, actionMsg: String, clickListener: View.OnClickListener?) {
        showSnackbar(msg, Snackbar.LENGTH_INDEFINITE, Color.BLUE, Color.WHITE, actionMsg, clickListener)
    }

    override fun showErrorMsg(msg: String, actionMsg: String, clickListener: View.OnClickListener?) {
        showSnackbar(msg, Snackbar.LENGTH_INDEFINITE, Color.RED, Color.WHITE, actionMsg, clickListener)
    }

    protected fun showSnackbar(message: String, length: Int, @ColorInt backgroundColor: Int, @ColorInt actionColor: Int,
                               actionMsg: String, clickListener: View.OnClickListener?) {
        val snackbar = createBaseSnackbar(message, length, backgroundColor)
        var listener = clickListener

        if (listener == null)
            listener = View.OnClickListener {}

        snackbar.setAction(actionMsg, listener)
        snackbar.setActionTextColor(actionColor)
        snackbar.show()
    }


    private fun showSnackbar(message: String, length: Int, backgroundColor: Int) {
        createBaseSnackbar(message, length, backgroundColor).show()
    }

    private fun createBaseSnackbar(message: String, length: Int, @ColorInt backgroundColor: Int): Snackbar {
        
        val viewGroup = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        val snackbar = Snackbar.make(viewGroup, message, length)
        val snackbarView = snackbar.view

        snackbarView.setBackgroundColor(backgroundColor)

        val snackTextView = snackbarView.findViewById<TextView>(R.id.snackbar_text)
        snackTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white))

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

}
