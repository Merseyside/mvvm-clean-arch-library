package com.merseyside.mvvmcleanarch.utils.animation

import android.animation.Animator
import android.animation.AnimatorSet

class AnimatorList(internal val approach: Approach) {

    enum class Approach { SEQUENTIALLY, TOGETHER }

    internal val list: MutableList<Animator> = ArrayList()

    fun addAnimator(animator: Animator) {
        list.add(animator)
    }

    fun addAnimatorList(animatorList: AnimatorList) {
        list.add(animatorList.toAnimatorSet())
    }
    
    fun toAnimatorSet(): AnimatorSet {
        return AnimatorSet().apply {
            when (approach) {
                Approach.SEQUENTIALLY -> playSequentially(list)
                Approach.TOGETHER -> playTogether(list)
            }
        }
    }
}