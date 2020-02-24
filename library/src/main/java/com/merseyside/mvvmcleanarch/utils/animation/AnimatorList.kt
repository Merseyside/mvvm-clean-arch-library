package com.merseyside.mvvmcleanarch.utils.animation

import android.animation.Animator
import android.animation.AnimatorSet

class AnimatorList(internal val approach: Approach): BaseAnimator() {

    internal val list: MutableList<BaseAnimator> = ArrayList()
    internal var animatorSet: AnimatorSet? = null

    fun addAnimator(animator: BaseAnimator) {
        list.add(animator)
    }

    override fun getAnimator(): Animator {
        return animatorSet ?: AnimatorSet().apply {
            when (approach) {
                Approach.SEQUENTIALLY -> playSequentially(list.map { it.getAnimator() })
                Approach.TOGETHER -> playTogether(list.map { it.getAnimator() })
            }

            animatorSet = this
        }
    }

    override fun setReverse(isReverse: Boolean) {
        animatorSet = null
        list.forEach { it.setReverse(isReverse) }
    }

    fun isEmpty() = list.isEmpty()

    fun isNotEmpty() = !isEmpty()
}