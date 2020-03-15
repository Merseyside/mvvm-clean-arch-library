package com.merseyside.mvvmcleanarch.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.text.TextUtils

class PreferenceManager {
    private val TAG = "PreferenceManager"

    class Builder {
        private var isShared = true
        private var filename: String? = null
        private var context: Context? = null

        fun setContext(context: Context?): Builder {
            this.context = context
            return this
        }

        fun setShared(value: Boolean): Builder {
            isShared = value
            return this
        }

        fun setFilename(filename: String?): Builder {
            this.filename = filename
            return this
        }

        @Throws(IllegalArgumentException::class)
        fun build(): PreferenceManager {
            requireNotNull(context) { "No context!" }
            return if (!isShared) {
                PreferenceManager(context!!)
            } else {
                if (TextUtils.isEmpty(filename)) throw IllegalArgumentException("Filename cannot be empty!") else {
                    PreferenceManager(context!!, filename)
                }
            }
        }
    }

    private var sharedPreferences: SharedPreferences

    private constructor(context: Context, preference_filename: String?) {
        sharedPreferences =
            context.getSharedPreferences(preference_filename, Context.MODE_PRIVATE)
    }

    private constructor(context: Context) {
        sharedPreferences =
            android.preference.PreferenceManager.getDefaultSharedPreferences(context)
    }

    operator fun contains(preference: String?): Boolean {
        return sharedPreferences.contains(preference)
    }

    fun setOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener?) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun removeOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener?) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun savePreference(preference: String?, value: Int) {
        sharedPreferences.edit().apply {
            putInt(preference, value)
            apply()
        }
    }

    fun savePreference(preference: String?, value: String?) {
        sharedPreferences.edit().apply {
            putString(preference, value)
            apply()
        }
    }

    fun savePreference(preference: String?, value: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(preference, value)
            apply()
        }
    }

    fun savePreference(preference: String?, value: Float) {
        sharedPreferences.edit().apply {
            putFloat(preference, value)
            apply()
        }
    }

    fun savePreference(preference: String?, value: Long) {
        sharedPreferences.edit().apply {
            putLong(preference, value)
            apply()
        }
    }

    fun getStringPreference(
        preference: String,
        default_value: String
    ): String {
        return sharedPreferences.getString(preference, default_value)!!
    }

    fun getStringPreference(
        preference: String
    ): String? {
        return sharedPreferences.getString(preference, null)
    }

    fun getBoolPreference(
        preference: String,
        default_value: Boolean
    ): Boolean {
        return sharedPreferences.getBoolean(preference, default_value)
    }

    fun getIntPreference(preference: String, default_value: Int): Int {
        return sharedPreferences.getInt(preference, default_value)
    }

    fun getLongPreference(preference: String, default_value: Long): Long {
        return sharedPreferences.getLong(preference, default_value)
    }

    fun getFloatPreference(preference: String, default_value: Float): Float {
        return sharedPreferences.getFloat(preference, default_value)
    }
}