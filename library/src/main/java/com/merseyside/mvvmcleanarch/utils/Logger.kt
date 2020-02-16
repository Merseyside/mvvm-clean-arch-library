package com.merseyside.mvvmcleanarch.utils

import android.util.Log
import com.merseyside.mvvmcleanarch.BuildConfig

object Logger {

    var isEnabled: Boolean = true
    var isDebugOnly = true

    fun log(tag: Any, msg: Any? = "Empty msg") {
        if (isEnabled && (!isDebugOnly || BuildConfig.DEBUG)) {
            Log.d(adoptTag(tag), adoptMsg(msg))
        }
    }

    fun logErr(tag: Any, msg: Any? = "Empty error") {
        if (isEnabled && (!isDebugOnly || BuildConfig.DEBUG)) {
            Log.e(adoptTag(tag), adoptMsg(msg))
        }
    }

    fun logInfo(tag: Any, msg: Any?) {
        if (isEnabled && (!isDebugOnly || BuildConfig.DEBUG)) {
            Log.i(adoptTag(tag), adoptMsg(msg))
        }
    }

    fun logWtf(tag: Any, msg: Any? = "wtf?") {
        if (isEnabled && (!isDebugOnly || BuildConfig.DEBUG)) {
            Log.wtf(adoptTag(tag), adoptMsg(msg))
        }
    }

    fun logErr(throwable: Throwable) {
        if (isEnabled && (!isDebugOnly || BuildConfig.DEBUG)) {
            throwable.printStackTrace()
        }
    }

    private fun adoptTag(tag: Any): String {
        val strTag = if (tag is String) {
            tag
        } else {
            tag.javaClass.simpleName
        }

        return if (strTag.isEmpty()) {
            "NonValidTag"
        } else {
            strTag
        }
    }

    private fun adoptMsg(msg: Any?): String {
       return when (msg) {
           null -> {
               "null"
           }
           is String -> {
               msg
           }
           else -> {
               msg.toString()
           }
       }
    }
}