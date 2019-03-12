package com.upstream.basemvvmimpl.presentation.model

abstract class BaseComparableAdapterViewModel<M : Comparable<M>> : BaseAdapterViewModel<M>() {

    abstract fun isContentTheSame(obj: M): Boolean
    abstract fun isItemsTheSame(obj: M): Boolean
    abstract fun compareTo(obj: M) : Int

}
