package com.merseyside.mvvmcleanarch.presentation.adapter

import com.merseyside.mvvmcleanarch.presentation.model.BaseAdapterViewModel

abstract class BaseSelectableAdapter<M: Any, T: BaseAdapterViewModel<M>> : BaseAdapter<M, T>() {

    interface OnItemSelectedListener<M> {
        fun onSelected(isSelected: Boolean, item: M)
    }

    private val listeners: MutableList<OnItemSelectedListener<M>> by lazy { ArrayList<OnItemSelectedListener<M>>() }

    private var selectedItem: T? = null
    set(value) {
        field = value

        val selectableItem = value as SelectableItemInterface

        listeners.forEach { listener ->
            listener.onSelected(selectableItem.isSelected, value.getItem())
        }
    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener<M>) {
        listeners.add(listener)
    }

    fun removeOnItemClickListener(listener: OnItemSelectedListener<M>) {
        listeners.remove(listener)
    }

    override fun add(model: T) {
        addItemToGroup(model)

        super.add(model)
    }

    override fun add(obj: M) {
        val isNoData = isEmpty()
        val item = createItemViewModel(obj)

        add(item)

        if (isNoData) {
            selectFirstSelectableItem()
        }
        notifyDataSetChanged()
    }

    override fun add(list: List<M>) {
        val isNoData = isEmpty()

        for (obj in list) {
            val item = createItemViewModel(obj)
            add(item)
        }

        if (isNoData) {
            selectFirstSelectableItem()
        }
        notifyDataSetChanged()
    }

    private fun selectFirstSelectableItem() {
        if (!isAllowToCancelSelection()) {

            getAllModels().forEach {item ->
                if (item is SelectableItemInterface) {
                    item.isSelected = true

                    selectedItem = item
                    return
                }
            }
        }
    }

    private fun addItemToGroup(item: T) {
        if (item is SelectableItemInterface) {
            item.setOnItemClickListener(object : OnItemClickListener<M> {
                override fun onItemClicked(obj: M) {

                    if (!item.isSelected) {

                        if (selectedItem != null) {
                            (selectedItem as SelectableItemInterface).isSelected = false
                        }

                        item.isSelected = true
                        selectedItem = item
                    } else if (isAllowToCancelSelection()) {
                        item.isSelected = false

                        selectedItem = null
                    }
                }
            })
        }
    }

    fun selectItem(item: M) {
        if (selectedItem == null || !selectedItem!!.areItemsTheSame(item)) {
            val found = find(item)

           if (found != null && found is SelectableItemInterface) {
                     selectedItem?.let {
                         (selectedItem as SelectableItemInterface).isSelected = false
                     }

                     found.isSelected = true
                     selectedItem = found
                 }

                 return
           }
    }

    fun selectItem(position: Int) {
        val item = getModelByPosition(position)
        if (item is SelectableItemInterface) {

            if (selectedItem == null || !selectedItem!!.areItemsTheSame(item.obj)) {
                selectedItem?.let {
                    (selectedItem as SelectableItemInterface).isSelected = false
                }

                item.isSelected = true

                selectedItem = item
            }
        }
    }

    fun getSelectedItem(): M? {
        return selectedItem?.getItem()
    }

    abstract fun isAllowToCancelSelection(): Boolean

    companion object {
        private const val TAG = "BaseSelectableAdapter"
    }
}