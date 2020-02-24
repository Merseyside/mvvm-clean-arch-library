package com.merseyside.mvvmcleanarch.utils.animation.animator

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import com.merseyside.mvvmcleanarch.utils.Logger
import com.merseyside.mvvmcleanarch.utils.animation.AnimAxis
import com.merseyside.mvvmcleanarch.utils.animation.AnimatorHelper
import com.merseyside.mvvmcleanarch.utils.animation.BaseAnimatorBuilder
import com.merseyside.mvvmcleanarch.utils.animation.BaseSingleAnimator
import com.merseyside.mvvmcleanarch.utils.time.TimeUnit

class SizeAnimator(builder: Builder) : BaseSingleAnimator(builder) {

    class Builder(
        view: View,
        duration: TimeUnit
    ) : BaseAnimatorBuilder<SizeAnimator>(view, duration) {

        var values: FloatArray? = null
        var animAxis: AnimAxis? = null

        fun setInPercents(values: FloatArray, animAxis: AnimAxis) {
            this.animAxis = animAxis

            this.values = getPixelsFromPercents(values, animAxis)
        }

        private fun getPixelsFromPercents(
            percents: FloatArray,
            animAxis: AnimAxis
        ) : FloatArray {

            val newValues = FloatArray(percents.size)

            val viewSize = when (animAxis) {
                AnimAxis.X_AXIS -> {
                    view.width
                }
                AnimAxis.Y_AXIS -> {
                    view.height
                }
            }

            var i = 0
            while (i < percents.size) {

                newValues[i] = (viewSize * percents[i])
                i++
            }

            Logger.log(this, newValues)

            return newValues
        }

        private fun changeSizeAnimation(
            floats: FloatArray,
            animAxis: AnimAxis,
            duration: TimeUnit
        ) : Animator {

            val array = floats.toMutableList()

            if (floats[0] == AnimatorHelper.CURRENT_VALUE) {
                when (animAxis) {
                    AnimAxis.Y_AXIS ->
                        array[0] = view.height.toFloat()
                    AnimAxis.X_AXIS -> {
                        array[0] = view.width.toFloat()
                    }

                }

            } else if (floats.size == 1) {

                when (animAxis) {
                    AnimAxis.Y_AXIS ->
                        array.add(0, view.height.toFloat())
                    AnimAxis.X_AXIS -> {
                        array.add(0, view.width.toFloat())
                    }

                }

                this.values = array.toFloatArray()
            }

            array.also { if (isReverse) it.reverse() }

            return ValueAnimator.ofFloat(*array.toFloatArray()).apply {
                this.duration = duration.toMillisLong()

                addUpdateListener { valueAnimator ->
                    val value = (valueAnimator.animatedValue as Float).toInt()

                    when (animAxis) {
                        AnimAxis.X_AXIS -> {
                            view.layoutParams.width = value
                        }

                        AnimAxis.Y_AXIS -> {
                            view.layoutParams.height = value
                        }
                    }
                }

            }
        }

        override fun build(): Animator {
            if (values != null && animAxis != null) {
                return changeSizeAnimation(values!!.copyOf(), animAxis!!, duration)
            } else {
                throw IllegalArgumentException("Points haven't been set")
            }
        }
    }
}