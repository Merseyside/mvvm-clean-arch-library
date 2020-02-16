package com.merseyside.mvvmcleanarch.utils.ext

fun Collection<*>?.isNotNullAndEmpty(): Boolean {
    return this != null && this.isNotEmpty()
}