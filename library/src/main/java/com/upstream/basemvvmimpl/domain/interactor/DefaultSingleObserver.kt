package com.upstream.basemvvmimpl.domain.interactor

import io.reactivex.observers.DisposableSingleObserver

open class DefaultSingleObserver<T> : DisposableSingleObserver<T>() {

    override fun onSuccess(obj: T) {
        // no-op by default
    }

    override fun onError(throwable: Throwable) {
        throwable.printStackTrace()
    }
}