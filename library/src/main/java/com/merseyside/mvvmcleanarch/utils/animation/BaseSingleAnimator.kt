package com.merseyside.mvvmcleanarch.utils.animation

import android.animation.Animator
import com.merseyside.mvvmcleanarch.utils.Logger

abstract class BaseSingleAnimator(
    val builder: BaseAnimatorBuilder<out BaseSingleAnimator>
): BaseAnimator() {
    
    var nativeAnimator: Animator? = null
    
    override fun setReverse(isReverse: Boolean) {
        if (builder.isReverse != isReverse) {
            nativeAnimator = null
            
            builder.isReverse = isReverse
        }
    }

    override fun getAnimator(): Animator {
        return if (nativeAnimator == null) {
            nativeAnimator = builder.build()

            return nativeAnimator!!
        } else {
            nativeAnimator!!
        }
    }
}