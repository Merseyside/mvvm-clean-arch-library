package com.upstream.basemvvmimpl.presentation.adapter

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.recyclerview.widget.SortedList
import com.upstream.basemvvmimpl.presentation.model.BaseComparableAdapterViewModel
import com.upstream.basemvvmimpl.utils.isContentEquals
import java.lang.reflect.ParameterizedType
import java.util.*
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparator
import kotlin.Int
import kotlin.String
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.set
import kotlin.synchronized
import com.upstream.basemvvmimpl.presentation.view.BaseViewHolder


abstract class BaseSortedAdapter<M: Any, T: BaseComparableAdapterViewModel<M>> : BaseAdapter<M, T>() {

    private val TAG = "BaseSortedAdapter"

    interface OnItemsAddListener {

        fun onItemsAdded()
    }

    interface OnItemUpdateListener {

        fun onItemsUpdated()
    }

    private val persistentClass: Class<T> = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<T>

    private var addThread: Thread? = null
    private var updateThread: Thread? = null
    private var filterThread: Thread? = null

    private val fullList: MutableList<T>
    private val list: SortedList<T>
    private var filteredList: MutableList<T>

    private val comparator : Comparator<T> = Comparator{ o1, o2 -> o1.compareTo(o2.getItem()) }

    private val lock = Any()

    private var isFiltered = false

    private val filtersMap = HashMap<String, Any>()
    private val notAppliedFiltersMap = HashMap<String, Any>()

    init {

        fullList = ArrayList()
        filteredList = ArrayList()

        list = SortedList(persistentClass, object : SortedList.Callback<T>() {
            override fun onInserted(position: Int, count: Int) {
                /* This bug google don't wanna fix for a long long time!*/
                runOnRightThread { notifyItemRangeInserted(position, count) }
            }

            override fun onRemoved(position: Int, count: Int) {
                runOnRightThread { notifyItemRangeRemoved(position, count) }
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                runOnRightThread { notifyItemMoved(fromPosition, toPosition) }
            }

            override fun compare(o1: T, o2: T): Int {
                return comparator.compare(o1, o2)
            }

            override fun onChanged(position: Int, count: Int) {
                runOnRightThread { notifyItemRangeChanged(position, count) }
            }

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(item1: T, item2: T): Boolean {
                return item1.isItemsTheSame(item2.getItem())
            }
        })
    }

    protected fun getList(): SortedList<T> {
        return list
    }

    override fun add(obj: M) {
        val listItem = createItemViewModel(obj)
        fullList.add(listItem)
        list.add(listItem)
    }

    override fun add(list: List<M>) {
        for (obj in list) {
            val listItem = createItemViewModel(obj)
            fullList.add(listItem)
        }

        runOnRightThread {
            this.list.beginBatchedUpdates()
            this.list.addAll(fullList)
            this.list.endBatchedUpdates()
        }
    }

    private fun runOnRightThread(func: () -> Unit) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Handler(Looper.getMainLooper()).post {
                func()
            }
        } else {
            func()
        }
    }

    fun addAsync(list: List<M>, onItemsAddListener: OnItemsAddListener? = null) {
        addThread = Thread {
            synchronized(lock) {
                add(list)

                onItemsAddListener?.onItemsAdded()

                //interruptThread(addThread)
                addThread = null
            }
        }
        addThread!!.start()

    }

    fun update(
        list: List<M>,
        isAddNew: Boolean = true,
        isDeleteOld: Boolean = false
    ) {
        if (!isFiltered) {
            val removeList = ArrayList<T>()
            for (i in 0 until this.list.size()) {
                var isFound = false
                val model = this.list.get(i)
                for (obj in list) {
                    if (model.isItemsTheSame(obj)) {
                        isFound = true
                        break
                    }
                }
                if (!isFound)
                    removeList.add(model)
            }

            if (isDeleteOld) {
                for (removeItem in removeList) {
                    remove(removeItem)
                }
            }
        }

        val addList = ArrayList<M>()
        for (obj in list) {
            if (updateThread == null || updateThread?.isInterrupted!!)
                if (!isFiltered && !update(obj)) {
                    addList.add(obj)
                }
        }

        if (isAddNew) add(addList)
    }

    private fun update(obj: M): Boolean {
        var isFound = false
        for (i in 0 until list.size()) {

            val model = list.get(i)
            if (updateThread == null || !updateThread!!.isInterrupted) {
                if (model.isItemsTheSame(obj)) {
                    if (!list.get(i).isContentTheSame(obj)) {

                        runOnRightThread {
                            notifyItemChanged(i, obj)
                        }
                    }
                    isFound = true
                    break
                }
            } else {
                break
            }
        }
        return isFound
    }

    fun updateAsync(
        list: List<M>,
        isAddNew: Boolean = true,
        isDeleteOld: Boolean = false,
        onItemsUpdateListener: OnItemUpdateListener? = null
    ) {
        updateThread = Thread {
            synchronized(lock) {
                update(list, isAddNew, isDeleteOld)

                onItemsUpdateListener?.onItemsUpdated()

                interruptThread(updateThread)
                updateThread = null
            }
        }
        updateThread!!.start()
    }

    private fun replaceAll(models: List<T>) {
        synchronized(lock) {
            list.beginBatchedUpdates()
            for (i in list.size() - 1 downTo 0) {
                val model = list.get(i)
                if (!models.contains(model)) {
                    list.remove(model)
                }
            }

            list.addAll(models)
            list.endBatchedUpdates()
        }
    }

    fun addFilter(key : String, obj : Any) {
        notAppliedFiltersMap[key] = obj

        if (filtersMap.containsKey(key)) {
            filtersMap.remove(key)
            notAppliedFiltersMap.putAll(filtersMap)
            filtersMap.clear()
            filteredList.clear()

            isFiltered = false
        }
    }

    fun removeFilter(key : String) {

        if (filtersMap.containsKey(key)) {
            filtersMap.remove(key)

            notAppliedFiltersMap.putAll(filtersMap)
            filtersMap.clear()

            filteredList.clear()
            isFiltered = false
        }

        notAppliedFiltersMap.remove(key)
    }

    fun applyFilters() {
        filterThread = Thread {
            synchronized(lock) {
                try {
                    if (!notAppliedFiltersMap.isEmpty()) {
                        val newList = ArrayList<T>()
                        val list: MutableList<T> = if (isFiltered) filteredList else fullList

                        for (item in list) {
                            if (filter(item, notAppliedFiltersMap)) {
                                newList.add(item)
                            }
                        }

                        if (!this.list.isContentEquals(newList))
                            setList(newList)

                        filteredList = newList
                        isFiltered = true

                        filtersMap.putAll(notAppliedFiltersMap)
                        notAppliedFiltersMap.clear()
                    }

                } catch (ignored: ConcurrentModificationException) {}
            }
        }
        filterThread!!.start()
    }

    override fun setFilter(query: String) {
        filterThread = Thread {
            try {
                if (!TextUtils.isEmpty(query)) {
                    isFiltered = true
                    val filteredList = ArrayList<T>()
                    for (obj in fullList) {
                        if (filter(obj, query))
                            filteredList.add(obj)
                    }
                    replaceAll(filteredList)
                } else {
                    isFiltered = false
                    replaceAll(fullList)
                }

            } catch (ignored: ConcurrentModificationException) {}
        }
        filterThread!!.start()
    }

    private fun setList(models: List<T>) {
        list.beginBatchedUpdates()
        list.clear()
        list.addAll(models)
        list.endBatchedUpdates()
    }

    fun clearFilters() {
        isFiltered = false

        filtersMap.clear()
        filteredList.clear()
        notAppliedFiltersMap.clear()

        setList(fullList)
    }

    override fun filter(obj: T, query: String): Boolean {
        return true
    }

    override fun filter(obj: T, filterMap : Map<String, Any>): Boolean {
        return true
    }

    override fun getItemCount(): Int {
        return list.size()
    }

    override fun removeAll() {
        interruptThread(addThread)
        addThread = null

        interruptThread(updateThread)
        updateThread = null

        interruptThread(filterThread)
        filterThread = null

        synchronized(lock) {
            list.beginBatchedUpdates()
            list.clear()
            list.endBatchedUpdates()
            fullList.clear()

            clearFilters()
        }
    }

    private fun remove(obj: T) {
        list.remove(obj)
        fullList.remove(obj)
    }

    private fun interruptThread(thread: Thread?) {

        if (thread != null && !thread.isInterrupted) {
            thread.interrupt()
        }
    }

    fun hasItems(): Boolean {
        return fullList.size != 0
    }

    @Throws(IllegalArgumentException::class)
    override fun getPositionOfObj(obj: M): Int {
        (0 until itemCount).forEach {
            if (list.get(it).getItem() == obj) return it
        }

        throw IllegalArgumentException("No data found")
    }

    @Throws(IllegalArgumentException::class)
    override fun notifyItemChanged(obj: M) {
        val index = getPositionOfObj(obj)

        notifyItemChanged(index, obj)
    }

    override fun getObjForPosition(position: Int): T {
        return list[position]
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, payloads: MutableList<Any>) {

        if (payloads.isNotEmpty()) {
            getList().get(position).setItem(payloads[0] as M)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
}
