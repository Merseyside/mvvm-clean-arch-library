package com.upstream.basemvvmimpl.presentation.model

import androidx.databinding.BaseObservable
import com.upstream.basemvvmimpl.presentation.adapter.BaseAdapter

abstract class BaseAdapterViewModel<M> : BaseObservable() {

    private var listener: BaseAdapter.AdapterClickListener? = null

    fun setAdapterListener(listener: BaseAdapter.AdapterClickListener?) {
        this.listener = listener
    }

    fun getClickListener(): BaseAdapter.AdapterClickListener? {
        return listener
    }

    abstract fun getItem(): M
}
