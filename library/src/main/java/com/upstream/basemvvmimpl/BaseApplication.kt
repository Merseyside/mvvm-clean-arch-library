package com.upstream.basemvvmimpl

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.annotation.StringRes
import com.upstream.basemvvmimpl.utils.LocaleManager


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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        context = localeManager.setLocale(this)

        Log.d(TAG, "Language changed ${newConfig.locale.language}")
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

    fun getActualString(@StringRes id: Int): String {
        return context.getString(id)
    }

    abstract fun getBaseLanguage(): String

    companion object {
        private const val TAG = "BaseApplication"
    }
}