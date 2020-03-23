package com.merseyside.mvvmcleanarch.utils.network

interface NetworkStateListener {

    fun onConnectionState(state: Boolean)
}