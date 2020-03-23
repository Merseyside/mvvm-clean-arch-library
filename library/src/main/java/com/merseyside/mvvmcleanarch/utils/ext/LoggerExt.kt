package com.merseyside.mvvmcleanarch.utils.ext

import com.merseyside.mvvmcleanarch.utils.Logger

fun <T> T.log(tag: Any = Logger.TAG, msg: Any? = this): T {
    Logger.log(tag, msg)

    return this
}