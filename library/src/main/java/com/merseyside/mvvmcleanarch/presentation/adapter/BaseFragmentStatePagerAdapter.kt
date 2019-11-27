package com.merseyside.mvvmcleanarch.presentation.adapter

import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

abstract class BaseFragmentStatePagerAdapter(
    fm: FragmentManager,
    behavior: Int
) : FragmentStatePagerAdapter(fm, behavior) {

    private val registeredFragments = SparseArray<Fragment>()

    private val registeredFragmentsCount: Int
        get() = registeredFragments.size()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, obj)
    }

    fun getRegisteredFragment(position: Int): Fragment {
        return registeredFragments.get(position)
    }

    abstract override fun getItem(position: Int): Fragment

    abstract override fun getCount(): Int

}
