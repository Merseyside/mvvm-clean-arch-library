package com.upstream.basemvvmimpl.presentation.model

abstract class BaseSortedAdapterViewModel<M> : BaseAdapterViewModel<M>(), Comparable<M> {

    abstract fun isContentTheSame(obj: M): Boolean
    abstract fun isItemsTheSame(obj: M): Boolean

}
