package com.merseyside.mvvmcleanarch.utils.ext

fun Collection<*>?.isNotNullAndEmpty(): Boolean {
    return this != null && this.isNotEmpty()
}

fun <T: Any> List<T>.removeEqualItems(): List<T> {
    return this.toSet().toList()
}