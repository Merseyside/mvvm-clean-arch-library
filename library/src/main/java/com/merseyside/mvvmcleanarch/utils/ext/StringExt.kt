package com.merseyside.mvvmcleanarch.utils.ext

fun String?.isNotNullAndEmpty(): Boolean {
    return this != null && this.isNotEmpty()
}