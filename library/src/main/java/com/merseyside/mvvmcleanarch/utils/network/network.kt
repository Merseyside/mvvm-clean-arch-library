package com.merseyside.mvvmcleanarch.utils.network

import android.content.Context

fun isOnline(context: Context): Boolean {
    return InternetConnectionObserver.isOnline(context)
}

fun isWifiOnAndConnected(context: Context): Boolean {
    return InternetConnectionObserver.isOnline(context)
}