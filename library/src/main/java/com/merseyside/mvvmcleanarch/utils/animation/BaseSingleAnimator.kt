package com.merseyside.mvvmcleanarch.utils.animation

import android.animation.Animator
import com.merseyside.mvvmcleanarch.utils.Logger

abstract class BaseSingleAnimator(
    val builder: BaseAnimatorBuilder<out BaseSingleAnimator>
): BaseAnimator() {
    
    var nativeAnimator: Animator? = null
    
    override fun setReverse(isReverse: Boolean) {
        Logger.log(this, "setReverse $isReverse")
        if (builder.isReverse != isReverse) {
            Logger.log(this, "setReverse !!!")
            nativeAnimator = null
            
            builder.isReverse = isReverse
        }
    }

    override fun getAnimator(): Animator {
        return if (nativeAnimator == null) {
            Logger.log(this, "create new animator")
            nativeAnimator = builder.build()

            getListeners().forEach {
                nativeAnimator!!.addListener(it)
            }

            return nativeAnimator!!
        } else {
            Logger.log(this, "use old animator")
            nativeAnimator!!
        }
    }
}