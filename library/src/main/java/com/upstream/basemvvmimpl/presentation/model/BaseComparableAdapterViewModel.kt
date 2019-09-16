package com.upstream.basemvvmimpl.presentation.model

abstract class BaseComparableAdapterViewModel<M> : BaseAdapterViewModel<M>() {

    abstract fun areContentTheSame(obj: M): Boolean

    abstract fun compareTo(obj: M) : Int

}
