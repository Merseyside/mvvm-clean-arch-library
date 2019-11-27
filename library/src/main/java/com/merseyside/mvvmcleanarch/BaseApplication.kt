package com.merseyside.mvvmcleanarch

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.annotation.StringRes
import com.merseyside.mvvmcleanarch.utils.LocaleManager

abstract class BaseApplication : Application() {

    private lateinit var localeManager: LocaleManager
    private lateinit var context: Context

    override fun attachBaseContext(base: Context) {
        localeManager = LocaleManager(base)

        context = if (localeManager.language.isEmpty()) {
            localeManager.setNewLocale(base, getBaseLanguage())
        } else {
            localeManager.setLocale(base)
        }
        super.attachBaseContext(context)
    }

    fun setLanguage(language: String): Context {
        context = localeManager.setNewLocale(this, language)
        return context
    }

    fun getLanguage(): String {
        return localeManager.language
    }

    fun getContext(): Context {
        return context
    }

    fun getActualString(@StringRes id: Int, vararg args: String): String {
        return context.getString(id, *args)
    }

    abstract fun getBaseLanguage(): String

    companion object {
        private const val TAG = "BaseApplication"
    }
}