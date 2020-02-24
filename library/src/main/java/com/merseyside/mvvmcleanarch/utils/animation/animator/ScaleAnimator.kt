package com.merseyside.mvvmcleanarch.utils.animation.animator

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import com.merseyside.mvvmcleanarch.utils.animation.AnimAxis
import com.merseyside.mvvmcleanarch.utils.animation.AnimatorHelper
import com.merseyside.mvvmcleanarch.utils.animation.BaseAnimatorBuilder
import com.merseyside.mvvmcleanarch.utils.animation.BaseSingleAnimator
import com.merseyside.mvvmcleanarch.utils.time.TimeUnit

class ScaleAnimator(builder: Builder) : BaseSingleAnimator(builder) {

    class Builder(
        view: View,
        duration: TimeUnit
    ) : BaseAnimatorBuilder<ScaleAnimator>(view, duration) {

        var values: FloatArray? = null
        var animAxis: AnimAxis? = null

        private fun scaleAnimation(
            floats: FloatArray,
            animAxis: AnimAxis,
            duration: TimeUnit
        ) : Animator {

            val values = when {
                floats[0] == AnimatorHelper.CURRENT_VALUE -> {
                    when (animAxis) {
                        AnimAxis.X_AXIS -> {
                            floats[0] = view.scaleX
                        }

                        AnimAxis.Y_AXIS -> {
                            floats[0] = view.scaleY
                        }
                    }

                    floats
                }

                floats.size == 1 -> {
                    val list = floats.toMutableList().apply {
                        when (animAxis) {
                            AnimAxis.X_AXIS -> {
                                add(0, view.scaleX)
                            }

                            AnimAxis.Y_AXIS -> {
                                add(0, view.scaleY)
                            }
                        }
                    }

                    list.toFloatArray().also { this.values = it }
                }

                else -> {
                    floats
                }
            }

            values.also { if (isReverse) it.reverse() }

            return ValueAnimator.ofFloat(*values).apply {
                this.duration = duration.toMillisLong()
                addUpdateListener { valueAnimator ->
                    val value = valueAnimator.animatedValue as Float

                    when (animAxis) {
                        AnimAxis.X_AXIS -> {
                            view.scaleX = value
                            view.requestLayout()
                        }

                        AnimAxis.Y_AXIS -> {
                            view.scaleY = value
                            view.requestLayout()
                        }
                    }
                }
            }
        }

        override fun build(): Animator {
            if (values != null && animAxis != null) {
                return scaleAnimation(values!!.copyOf(), animAxis!!, duration)
            } else {
                throw IllegalArgumentException("Points haven't been set")
            }
        }

    }
}