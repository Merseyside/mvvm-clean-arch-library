package com.merseyside.mvvmcleanarch.utils.animation.animator

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import com.merseyside.mvvmcleanarch.utils.animation.BaseAnimatorBuilder
import com.merseyside.mvvmcleanarch.utils.animation.BaseSingleAnimator
import com.merseyside.mvvmcleanarch.utils.time.TimeUnit

class ColorAnimator(builder: Builder) : BaseSingleAnimator(builder) {

    class Builder(
        view: View,
        duration: TimeUnit
    ) : BaseAnimatorBuilder<ColorAnimator>(view, duration) {

        var values: IntArray? = null

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun rgbsAnimation(
            ints: IntArray,
            duration: TimeUnit
        ): Animator {

            val values = if (ints.size == 1) {
                ints.toMutableList().apply {
                    val background: Drawable = view.background
                    if (background is ColorDrawable) {
                        add(0, background.color)
                    }
                }.toIntArray().also {
                    this.values = it
                }

            } else {
                ints
            }

            values.also { if (isReverse) it.reverse() }

            return ValueAnimator.ofArgb(*values).apply {
                this.duration = duration.toMillisLong()

                    addUpdateListener { valueAnimator ->
                        val value = valueAnimator.animatedValue as Int

                        view.setBackgroundColor(value)
                    }
                }
            }

        override fun build(): Animator {
            if (values != null) {
                return rgbsAnimation(values!!.copyOf(), duration)
            } else {
                throw IllegalArgumentException("Points haven't been set")
            }
        }
    }

}