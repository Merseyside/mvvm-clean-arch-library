package com.merseyside.mvvmcleanarch.presentation.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.merseyside.mvvmcleanarch.presentation.activity.Orientation
import com.merseyside.mvvmcleanarch.presentation.fragment.BaseFragment
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar

interface IActivityView : IView {

    fun getContext(): Context

    fun hideKeyboard(context: Context? = getContext(), view: View)

    fun registerKeyboardListener(listener: OnKeyboardStateListener): Unregistrar

    fun onBackPressed()

    fun getLanguage(): String?

    fun setFragmentToolbar(toolbar: Toolbar?)

    fun setFragmentResult(fragmentResult: BaseFragment.FragmentResult)
}