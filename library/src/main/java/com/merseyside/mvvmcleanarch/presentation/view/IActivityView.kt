package com.merseyside.mvvmcleanarch.presentation.view

import androidx.appcompat.widget.Toolbar
import com.merseyside.mvvmcleanarch.presentation.fragment.BaseFragment
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar

interface IActivityView : IView {

    fun hideKeyboard()

    fun registerKeyboardListener(listener: OnKeyboardStateListener): Unregistrar

    fun onBackPressed()

    fun getLanguage(): String?

    fun setFragmentToolbar(toolbar: Toolbar?)

    fun setFragmentResult(fragmentResult: BaseFragment.FragmentResult)
}