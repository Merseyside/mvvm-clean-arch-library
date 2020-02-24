package com.merseyside.mvvmcleanarch.utils.animation.animator

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import com.merseyside.mvvmcleanarch.utils.animation.BaseAnimatorBuilder
import com.merseyside.mvvmcleanarch.utils.animation.BaseSingleAnimator
import com.merseyside.mvvmcleanarch.utils.time.TimeUnit

class AlphaAnimator(builder: Builder) : BaseSingleAnimator(builder) {

    class Builder(
        view: View,
        duration: TimeUnit
    ): BaseAnimatorBuilder<AlphaAnimator>(view, duration) {

        var values: FloatArray? = null

        private fun alphaAnimation(
            values: FloatArray,
            duration: TimeUnit
        ): Animator {

            var values = when {
                values[0] == CURRENT_FLOAT -> {
                    values[0] = view.alpha
                    values
                }

                values.size == 1 -> {
                    val list = values.toMutableList().apply {
                        add(0, view.alpha)
                    }

                    list.toFloatArray().also { this.values = it }
                }

                else -> {
                    values
                }
            }

            if (isReverse) values.reverse()

            return ValueAnimator.ofFloat(*values).apply {
                this.duration = duration.toMillisLong()

                var previousValue: Float? = values[0]

                addUpdateListener { valueAnimator ->

                    val value = valueAnimator.animatedValue as Float

                    view.alpha = value

                    if (previousValue != value) {

                        if (previousValue == 0f && previousValue?.compareTo(value) == -1) {
                            view.visibility = View.VISIBLE
                        } else if (previousValue?.compareTo(value) == 1 && value == 0f) {
                            view.visibility = View.INVISIBLE
                        }

                    }

                    previousValue = value
                }
            }
        }


        override fun build(): Animator {
            if (values != null) {
                return alphaAnimation(values!!.copyOf(), duration)
            } else {
                throw IllegalArgumentException("Points haven't been set")
            }
        }
    }
}