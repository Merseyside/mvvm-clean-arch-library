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

    override val modelList: MutableList<T> = ArrayList()
    private  val sortedList: SortedList<T>
    private  var filteredList: MutableList<T> = ArrayList()

    private var addThread: Thread? = null
    private var updateThread: Thread? = null
    private var filterThread: Thread? = null

    private val comparator : Comparator<T> = Comparator{ o1, o2 -> o1.compareTo(o2.getItem()) }

    private val lock = Any()

    private var isFiltered = false

    private val filtersMap by lazy { HashMap<String, Any>() }
    private val notAppliedFiltersMap by lazy {HashMap<String, Any>() }

    init {

        sortedList = SortedList(persistentClass, object : SortedList.Callback<T>() {
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
                return oldItem.areContentsTheSame(newItem.getItem())
            }

            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return oldItem.areItemsTheSame(newItem.getItem())
            }
        })
    }

    private fun getFullList(): SortedList<T> {
        return sortedList
    }

    override fun add(obj: M) {
        val listItem = createItemViewModel(obj)
        modelList.add(listItem)
        sortedList.add(listItem)
    }

    override fun add(list: List<M>) {
        for (obj in list) {
            val listItem = createItemViewModel(obj)
            modelList.add(listItem)
        }

        runOnRightThread {
            sortedList.beginBatchedUpdates()
            sortedList.addAll(modelList)
            sortedList.endBatchedUpdates()
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
                val removeList = (0 until sortedList.size()).map {
                    sortedList.get(it)
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
        }

        val addList = ArrayList<M>()
        for (obj in updateRequest.list!!) {
            if (Looper.getMainLooper() == Looper.myLooper() || (updateThread != null && !updateThread!!.isInterrupted)) {
                if (!isFiltered && !update(obj)) {
                    addList.add(obj)
                }
            } else {
                break
            }
        }

        if (updateRequest.isAddNew) add(addList)
    }

    private fun update(obj: M): Boolean {
        var isFound = false
        for (i in 0 until sortedList.size()) {

            val model = sortedList.get(i)
            if (model.areItemsTheSame(obj)) {
                if (!sortedList.get(i).areContentsTheSame(obj)) {
                    runOnRightThread {
                        notifyItemChanged(i, obj)
                    }
                }
                isFound = true
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
            sortedList.beginBatchedUpdates()
            for (i in sortedList.size() - 1 downTo 0) {
                val model = sortedList.get(i)
                if (!models.contains(model)) {
                    sortedList.remove(model)
                }
            }

            sortedList.addAll(models)
            sortedList.endBatchedUpdates()
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
                        val list: MutableList<T> = if (isFiltered) filteredList else modelList

                        filteredList = list.filter { filter(it, notAppliedFiltersMap) }.toMutableList()

                        if (!this.sortedList.isContentEquals(filteredList)) setList(filteredList)

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
                    for (obj in modelList) {
                        if (filter(obj, query)) filteredList.add(obj)
                    }
                    replaceAll(filteredList)
                } else {
                    isFiltered = false
                    replaceAll(modelList)
                }

            } catch (ignored: ConcurrentModificationException) {}
        }.apply { start() }
    }

    private fun setList(list: List<T>) {
        sortedList.beginBatchedUpdates()
        sortedList.clear()
        sortedList.addAll(list)
        sortedList.endBatchedUpdates()
    }

    fun clearFilters() {
        isFiltered = false

        filtersMap.clear()
        filteredList.clear()
        notAppliedFiltersMap.clear()

        setList(modelList)
    }

    override fun filter(obj: T, query: String): Boolean {
        return true
    }

    override fun filter(obj: T, filterMap : Map<String, Any>): Boolean {
        return true
    }

    override fun getItemCount(): Int {
        return sortedList.size()
    }

    override fun removeAll() {
        interruptThread(addThread)
        addThread = null

        interruptThread(updateThread)
        updateThread = null

        interruptThread(filterThread)
        filterThread = null

        synchronized(lock) {
            sortedList.beginBatchedUpdates()
            sortedList.clear()
            sortedList.endBatchedUpdates()
            modelList.clear()

            clearFilters()
        }
    }

    override fun remove(obj: M) {
        val foundObj = (0 until sortedList.size())
            .asSequence()
            .map { sortedList.get(it) }
            .firstOrNull { it.areItemsTheSame(obj) }

        if (foundObj != null) {
            remove(foundObj)
        }
    }

    override fun remove(list: List<M>) {
        list.forEach { remove(it) }
    }

    private fun remove(obj: T) {
        sortedList.remove(obj)
        modelList.remove(obj)
        filteredList.remove(obj)
    }

    private fun interruptThread(thread: Thread?) {

        if (thread != null && !thread.isInterrupted) {
            thread.interrupt()
        }
    }

    override fun isEmpty(): Boolean {
        return modelList.isEmpty()
    }

    override fun getModelByPosition(position: Int): T {
        return sortedList[position]
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, payloads: MutableList<Any>) {

        if (payloads.isNotEmpty()) {
            getFullList().get(position).setItem(payloads[0] as M)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
    
    @Throws(IndexOutOfBoundsException::class)
    override fun first(): M {
        try {
            return getModelByPosition(0).getItem()
        } catch (e: Exception) {
            throw IndexOutOfBoundsException("List is empty")
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    override fun last(): M {
        try {
            return getModelByPosition(itemCount - 1).getItem()
        } catch (e: Exception) {
            throw IndexOutOfBoundsException("List is empty")
        }
    }

    override fun getAll(): List<M> {
        return modelList.map { it.obj }
    }

    override fun getAllModels(): List<T> {
        return modelList
    }

    companion object {
        private const val TAG = "BaseSortedAdapter"
    }
}
