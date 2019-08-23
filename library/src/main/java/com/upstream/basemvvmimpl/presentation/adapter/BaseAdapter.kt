package com.upstream.basemvvmimpl.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.upstream.basemvvmimpl.presentation.model.BaseAdapterViewModel
import com.upstream.basemvvmimpl.presentation.view.BaseViewHolder
import java.lang.IllegalArgumentException
import java.util.*

abstract class BaseAdapter<M, T : BaseAdapterViewModel<M>> : RecyclerView.Adapter<BaseViewHolder>() {

    private var listener: AdapterClickListener? = null

    private val list: MutableList<T> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val binding : ViewDataBinding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false)

        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val obj = getObjForPosition(position)
        obj.setAdapterListener(listener)
        holder.bind(getBindingVariable(), obj)
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition(position)
    }

    open fun getObjForPosition(position: Int): T {
        return list[position]
    }

    protected abstract fun getLayoutIdForPosition(position: Int): Int

    protected abstract fun getBindingVariable(): Int

    fun setOnItemClickListener(listener: AdapterClickListener) {
        this.listener = listener
    }

    interface AdapterClickListener {
        fun onItemClicked(obj: Any)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    open fun add(obj: M) {
        list.add(createItemViewModel(obj))
    }

    open fun add(list: List<M>) {
        for (obj in list) {
            add(obj)
        }
        notifyDataSetChanged()
    }

    open fun removeAll() {
        list.clear()
        notifyDataSetChanged()
    }

    open fun getPositionOfObj(obj: M): Int {
        list.forEachIndexed { index, t ->
            if (t.getItem() == obj) return index
        }

        throw IllegalArgumentException("No data found")
    }

    open fun notifyItemChanged(obj: M) {
        val index = getPositionOfObj(obj)

        notifyItemChanged(index, obj)
    }

    abstract fun setFilter(query: String)

    open fun filter(obj: T, query: String): Boolean {
        return true
    }

    open fun filter(obj: T, filterMap : Map<String, Any>): Boolean {
        return true
    }

    protected abstract fun createItemViewModel(obj: M): T
}
