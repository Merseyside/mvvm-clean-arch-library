package com.merseyside.mvvmcleanarch.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener

class PreferenceManager private constructor(
    context: Context,
    preference_filename: String
) {

    class Builder(private val context: Context) {
        private var filename: String? = null

        fun setFilename(filename: String): Builder {
            this.filename = filename
            return this
        }

        @Throws(IllegalArgumentException::class)
        fun build(): PreferenceManager {
            return PreferenceManager(context,
                filename ?: "${context.packageName}.prefs"
            )
        }
    }

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(preference_filename, Context.MODE_PRIVATE)

    operator fun contains(preference: String?): Boolean {
        return sharedPreferences.contains(preference)
    }

    fun setOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener?) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun removeOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener?) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun put(preference: String?, value: Int) {
        sharedPreferences.edit().apply {
            putInt(preference, value)
            apply()
        }
    }

    fun put(preference: String?, value: String?) {
        sharedPreferences.edit().apply {
            putString(preference, value)
            apply()
        }
    }

    fun put(preference: String?, value: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(preference, value)
            apply()
        }
    }

    fun put(preference: String?, value: Float) {
        sharedPreferences.edit().apply {
            putFloat(preference, value)
            apply()
        }
    }

    fun put(preference: String?, value: Long) {
        sharedPreferences.edit().apply {
            putLong(preference, value)
            apply()
        }
    }

    fun getString(
        preference: String,
        default_value: String
    ): String {
        return sharedPreferences.getString(preference, default_value)!!
    }

    fun getString(
        preference: String
    ): String? {
        return sharedPreferences.getString(preference, null)
    }

    fun getBool(
        preference: String,
        default_value: Boolean
    ): Boolean {
        return sharedPreferences.getBoolean(preference, default_value)
    }

    fun getInt(preference: String, default_value: Int): Int {
        return sharedPreferences.getInt(preference, default_value)
    }

    fun getLong(preference: String, default_value: Long): Long {
        return sharedPreferences.getLong(preference, default_value)
    }

    fun getFloat(preference: String, default_value: Float): Float {
        return sharedPreferences.getFloat(preference, default_value)
    }
}