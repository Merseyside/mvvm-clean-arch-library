package com.upstream.basemvvmimpl.presentation.model

import androidx.databinding.BaseObservable
import com.upstream.basemvvmimpl.presentation.adapter.BaseAdapter

abstract class BaseAdapterViewModel<M>(
    obj: M
) : BaseObservable() {

    abstract var obj: M

    init {
        this.obj = obj
    }

    private val listeners: MutableList<BaseAdapter.OnItemClickListener<M>> by lazy { ArrayList<BaseAdapter.OnItemClickListener<M>>() }

    fun setOnItemClickListener(listener: BaseAdapter.OnItemClickListener<M>) {
        this.listeners.add(listener)
    }

    fun removeOnItemClickListener(listener: BaseAdapter.OnItemClickListener<M>) {
        listeners.remove(listener).toString()
    }

    fun onClick() {
        if (listeners.isNotEmpty()) {
            listeners.forEach { it.onItemClicked(obj) }
        }
    }

    open fun setItem(item: M) {
        this.obj = item
        notifyUpdate()
    }

    fun getItem(): M {
        return obj
    }

    abstract fun areItemsTheSame(obj: M): Boolean

    abstract fun notifyUpdate()

    companion object {
        private const val TAG = "BaseAdapterViewModel"
    }
}
