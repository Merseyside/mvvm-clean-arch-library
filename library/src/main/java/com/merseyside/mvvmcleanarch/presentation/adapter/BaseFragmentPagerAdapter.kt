package com.merseyside.mvvmcleanarch.presentation.adapter

import android.os.Bundle
import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

abstract class BaseFragmentPagerAdapter(
    private val fm: FragmentManager,
    behavior: Int = BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) : FragmentPagerAdapter(fm, behavior) {

    private val registeredFragments = SparseArray<Fragment>()

    val registeredFragmentsCount: Int
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

    fun getRegisteredFragment(position: Int): Fragment? {
        return try {
            registeredFragments.get(position)
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    fun saveFragments(outState: Bundle?) {
        if (outState != null) {
            outState.putInt(FRAGMENT_COUNT_KEY, registeredFragmentsCount)

            for (i in 0 until registeredFragmentsCount) {
                fm.putFragment(outState, "pager_fragment_$i", registeredFragments[i])
            }
        }
    }

    fun restoreFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val count = savedInstanceState.getInt(FRAGMENT_COUNT_KEY)

            for (i in 0 until count) {
                val fragment = fm.getFragment(savedInstanceState, "pager_fragment_$i")

                if (fragment != null) {
                    registeredFragments.put(i, fragment)
                }
            }
        }
    }

    companion object {
        private const val FRAGMENT_COUNT_KEY = "pager_fragment_count"
    }
}
