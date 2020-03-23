package com.merseyside.mvvmcleanarch.presentation.view.localeViews

import android.content.Context
import android.view.View
import com.merseyside.mvvmcleanarch.presentation.interfaces.IStringHelper

interface ILocaleView : IStringHelper {

    fun updateLocale(context: Context = getLocaleContext())

    fun getView(): View
}