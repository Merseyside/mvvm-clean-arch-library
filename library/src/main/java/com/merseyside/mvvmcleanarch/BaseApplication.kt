package com.merseyside.mvvmcleanarch

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.annotation.StringRes
import com.merseyside.mvvmcleanarch.utils.LocaleManager
import com.merseyside.mvvmcleanarch.utils.getLocalizedContext
import java.util.*

abstract class BaseApplication : Application() {

    private lateinit var localeManager: LocaleManager
    lateinit var context: Context
    private set

    override fun attachBaseContext(base: Context) {
        localeManager = LocaleManager(base)
        context = getLocalizedContext(localeManager)

        super.attachBaseContext(context)
    }

    fun setLanguage(language: String): Context {
        context = localeManager.setNewLocale(language)
        return context
    }

    fun getLanguage(): String {
        return localeManager.language
    }

    fun getActualString(@StringRes id: Int, vararg args: String): String {
        return context.getString(id, *args)
    }

    fun getLocale(): Locale {
        return localeManager.getCurrentLocale()
    }

    companion object {
        private const val TAG = "BaseApplication"
    }
}