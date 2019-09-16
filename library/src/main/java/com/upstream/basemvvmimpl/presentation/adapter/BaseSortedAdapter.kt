package com.upstream.basemvvmimpl.presentation.adapter

import android.os.Handler
import android.os.Looper
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
import kotlin.collections.ArrayList


@Suppress("UNCHECKED_CAST")
abstract class BaseSortedAdapter<M: Any, T: BaseComparableAdapterViewModel<M>> : BaseAdapter<M, T>() {

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

    private val fullList: MutableList<T> = ArrayList()
    private val list: SortedList<T>
    private var filteredList: MutableList<T> = ArrayList()

    private val comparator : Comparator<T> = Comparator{ o1, o2 -> o1.compareTo(o2.getItem()) }

    private val lock = Any()

    private var isFiltered = false

    private val filtersMap by lazy { HashMap<String, Any>() }
    private val notAppliedFiltersMap by lazy {HashMap<String, Any>() }

    init {

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
                return oldItem.areContentTheSame(newItem.getItem())
            }

            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return oldItem.areItemsTheSame(newItem.getItem())
            }
        })
    }

    private fun getList(): SortedList<T> {
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
                addThread = null
            }
        }.apply { start() }
    }

    fun update(updateRequest: UpdateRequest<M>) {
        if (!isFiltered) {

            if (updateRequest.isDeleteOld) {
                val removeList = (0 until this.list.size()).map {
                    this.list.get(it)
                }.filter {
                    var isFound = false

                    for (obj in updateRequest.list!!) {
                        if (it.areItemsTheSame(obj)) {
                            isFound = true
                            break
                        }
                    }

                    !isFound
                }

                for (removeItem in removeList) {
                    remove(removeItem)
                }
            }

//            for (i in 0 until this.list.size()) {
//                var isFound = false
//                val model = this.list.get(i)
//                for (obj in list) {
//                    if (model.areItemsTheSame(obj)) {
//                        isFound = true
//                        break
//                    }
//                }
//
//                if (isDeleteOld && !isFound) removeList.add(model)
//            }
//
//            if (isDeleteOld) {
//                for (removeItem in removeList) {
//                    remove(removeItem)
//                }
//            }
        }

        val addList = ArrayList<M>()
        for (obj in updateRequest.list!!) {
            if (updateThread == null || updateThread?.isInterrupted!!)
                if (!isFiltered && !update(obj)) {
                    addList.add(obj)
                }
        }

        if (updateRequest.isAddNew) add(addList)
    }

    private fun update(obj: M): Boolean {
        var isFound = false
        for (i in 0 until list.size()) {

            val model = list.get(i)
            if (updateThread == null || !updateThread!!.isInterrupted) {
                if (model.areItemsTheSame(obj)) {
                    if (!list.get(i).areContentTheSame(obj)) {

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
        updateRequest: UpdateRequest<M>,
        onItemsUpdateListener: OnItemUpdateListener? = null
    ) {
        updateThread = Thread {
            synchronized(lock) {
                update(updateRequest)

                onItemsUpdateListener?.onItemsUpdated()

                interruptThread(updateThread)
                updateThread = null
            }
        }.apply { start() }
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
                    if (notAppliedFiltersMap.isNotEmpty()) {
                        val list: MutableList<T> = if (isFiltered) filteredList else fullList

                        filteredList = list.filter { filter(it, notAppliedFiltersMap) }.toMutableList()

                        if (!this.list.isContentEquals(filteredList)) setList(filteredList)

                        isFiltered = true

                        filtersMap.putAll(notAppliedFiltersMap)
                        notAppliedFiltersMap.clear()
                    }

                } catch (ignored: ConcurrentModificationException) {}
            }
        }.apply { start() }
    }

    override fun setFilter(query: String) {
        filterThread = Thread {
            try {
                if (query.isNotEmpty()) {
                    isFiltered = true
                    val filteredList = ArrayList<T>()
                    for (obj in fullList) {
                        if (filter(obj, query)) filteredList.add(obj)
                    }
                    replaceAll(filteredList)
                } else {
                    isFiltered = false
                    replaceAll(fullList)
                }

            } catch (ignored: ConcurrentModificationException) {}
        }.apply { start() }
    }

    private fun setList(list: List<T>) {
        this.list.beginBatchedUpdates()
        this.list.clear()
        this.list.addAll(list)
        this.list.endBatchedUpdates()
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

    override fun remove(obj: M) {
        val foundObj = (0 until list.size())
            .asSequence()
            .map { list.get(it) }
            .firstOrNull { it.areItemsTheSame(obj) }

        if (foundObj != null) {
            remove(foundObj)
        }
    }

    override fun remove(list: List<M>) {
        list.forEach { remove(it) }
    }

    private fun remove(obj: T) {
        list.remove(obj)
        fullList.remove(obj)
        filteredList.remove(obj)
    }

    private fun interruptThread(thread: Thread?) {

        if (thread != null && !thread.isInterrupted) {
            thread.interrupt()
        }
    }

    override fun hasItems(): Boolean {
        return fullList.isNotEmpty()
    }

    @Throws(IllegalArgumentException::class)
    override fun getPositionOfObj(obj: M): Int {
        (0 until itemCount).forEach {
            if (list.get(it).getItem() == obj) return it
        }

        throw IllegalArgumentException("No data found")
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

    companion object {
        private const val TAG = "BaseSortedAdapter"
    }
}
