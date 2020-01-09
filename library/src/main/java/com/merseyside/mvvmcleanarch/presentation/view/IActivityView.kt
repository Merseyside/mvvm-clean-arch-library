package com.merseyside.mvvmcleanarch.presentation.view

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.merseyside.mvvmcleanarch.presentation.fragment.BaseFragment
import com.merseyside.mvvmcleanarch.utils.SnackbarManager

interface IActivityView : IView {

    fun hideKeyboard()

    fun onBackPressed()

    fun getLanguage(): String?

    fun setFragmentToolbar(toolbar: Toolbar?)

    fun setFragmentResult(fragmentResult: BaseFragment.FragmentResult)
}