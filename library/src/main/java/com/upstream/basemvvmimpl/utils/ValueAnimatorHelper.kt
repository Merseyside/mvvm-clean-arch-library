package com.upstream.basemvvmimpl.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.os.Build
import android.util.Log
import android.view.View
import java.util.*

class ValueAnimatorHelper {

    enum class AnimAxis {X_AXIS, Y_AXIS}

    enum class MainPoint {CENTER, TOP_LEFT, TOP_RIGHT, DOWN_LEFT, DOWN_RIGHT}

    //Add percentage implementation

    val animationsList : MutableList<Animator> = ArrayList()
    val animatorSet: AnimatorSet = AnimatorSet()


    class Builder(val view: View) {

        lateinit var valueAnimator: ValueAnimator
            private set

        private fun recalculateValues(values: FloatArray, mainPoint: MainPoint, animAxis: AnimAxis) : FloatArray {
            if (mainPoint != MainPoint.TOP_LEFT) {
                val viewSize = when (animAxis) {
                    AnimAxis.X_AXIS -> {
                        view.width
                    }
                    AnimAxis.Y_AXIS -> {
                        view.height
                    }
                }

                var i = 1
                while (i < values.size) {

                    when (mainPoint) {
                        MainPoint.CENTER -> {
                            values[i] = values[i] - viewSize / 2
                        }

                        MainPoint.TOP_RIGHT -> {
                            when (animAxis) {
                                AnimAxis.X_AXIS ->
                                    values[i] = values[i] + viewSize
                                AnimAxis.Y_AXIS -> {}
                            }
                        }

                        MainPoint.DOWN_LEFT -> {
                            when (animAxis) {
                                AnimAxis.X_AXIS -> {}

                                AnimAxis.Y_AXIS -> {
                                    values[i] = values[i] - viewSize
                                }
                            }
                        }

                        MainPoint.DOWN_RIGHT -> {
                            when (animAxis) {
                                AnimAxis.X_AXIS -> {
                                    values[i] = values[i] - viewSize
                                }
                                AnimAxis.Y_AXIS -> {
                                    values[i] = values[i] - viewSize
                                }
                            }
                        }
                        else -> {}
                    }
                    i++
                }
            }

            return values
        }

        fun translateAnimationPercent(vararg percents: Float, mainPoint: MainPoint = MainPoint.CENTER,
                                      animAxis: AnimAxis, duration: Long, updateListener : ValueAnimator.AnimatorUpdateListener? = null,
                                      animatorListener: Animator.AnimatorListener? = null) : Builder {

            val newValues = FloatArray(percents.size)

            val viewSize = when (animAxis) {
                AnimAxis.X_AXIS -> {
                    (view.parent as View).width
                }
                AnimAxis.Y_AXIS -> {
                    (view.parent as View).height
                }
            }

            var i = 0
            while (i < percents.size) {

                newValues[i] = viewSize * percents[i]
                i++
            }

            return translateAnimation(*newValues, mainPoint = mainPoint, animAxis = animAxis,
                duration = duration, listener = updateListener, animatorListener = animatorListener)
        }

        fun translateAnimation(vararg floats: Float, mainPoint: MainPoint = MainPoint.CENTER, animAxis: AnimAxis,
                               duration: Long, listener : ValueAnimator.AnimatorUpdateListener? = null,
                               animatorListener: Animator.AnimatorListener? = null) : Builder {

            var array = floats.toMutableList()

            if (floats.size == 1) {

                when (animAxis) {
                    AnimAxis.Y_AXIS ->
                        array.add(0, view.y)
                    AnimAxis.X_AXIS ->
                        array.add(0, view.x)
                }
            }

            array = recalculateValues(array.toFloatArray(), mainPoint, animAxis).toMutableList()

            valueAnimator = ValueAnimator.ofFloat(*array.toFloatArray()).apply {
                this.duration = duration
                if (listener == null) {
                    addUpdateListener { valueAnimator ->
                        val value = valueAnimator.animatedValue as Float
                        when (animAxis) {
                            AnimAxis.X_AXIS ->
                                view.x = value

                            AnimAxis.Y_AXIS ->
                                view.y = value
                        }

                        view.requestLayout()
                    }
                } else {
                    addUpdateListener(listener)
                }
            }

            if (animatorListener != null)
                valueAnimator.addListener(animatorListener)

            return this
        }

        fun changeSizeAnimationPercent(vararg percents: Float, animAxis: AnimAxis, duration: Long,
                                       listener : ValueAnimator.AnimatorUpdateListener? = null,
                                       animatorListener: Animator.AnimatorListener? = null) : Builder {

            val newValues = IntArray(percents.size)

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

                newValues[i] = (viewSize * percents[i]).toInt()
                i++
            }

            return changeSizeAnimation(*newValues, animAxis = animAxis, duration = duration, listener = listener, animatorListener = animatorListener)
        }

        fun changeSizeAnimation(vararg ints: Int, animAxis: AnimAxis, duration: Long,
                                listener : ValueAnimator.AnimatorUpdateListener? = null,
                                animatorListener: Animator.AnimatorListener? = null) : Builder {

            var array = ints.toMutableList()

            if (ints.size == 1) {

                when (animAxis) {
                    AnimAxis.Y_AXIS ->
                        array.add(0, view.height)
                    AnimAxis.X_AXIS -> {
                        array.add(0, view.width)
                    }

                }
            }

            valueAnimator = ValueAnimator.ofInt(*array.toIntArray()).apply {
                this.duration = duration
                if (listener == null) {
                    addUpdateListener { valueAnimator ->
                        val value = valueAnimator.animatedValue as Int

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

            return this
        }

        fun scaleAnimation(vararg floats: Float, animAxis: AnimAxis, duration: Long,
                           listener : ValueAnimator.AnimatorUpdateListener? = null,
                           animatorListener: Animator.AnimatorListener? = null) : Builder {
            valueAnimator = ValueAnimator.ofFloat(*floats).apply {
                this.duration = duration
                if (listener == null) {
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
                } else {
                    addUpdateListener(listener)
                }
            }

            if (animatorListener != null)
                valueAnimator.addListener(animatorListener)

            return this
        }

        fun alphaAnimation(vararg floats: Float, duration: Long,
                           listener : ValueAnimator.AnimatorUpdateListener? = null,
                           animatorListener: Animator.AnimatorListener? = null) : Builder {
            valueAnimator = ValueAnimator.ofFloat(*floats).apply {
                this.duration = duration

                var previousValue: Float? = floats[0]

                if (listener == null) {
                    addUpdateListener {

                        valueAnimator ->

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
                } else {
                    addUpdateListener(listener)
                }
            }

            if (animatorListener != null)
                valueAnimator.addListener(animatorListener)

            return this
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun rgbsAnimation(vararg ints: Int, duration: Long, listener : ValueAnimator.AnimatorUpdateListener? = null,
                          animatorListener: Animator.AnimatorListener? = null) : Builder {
            valueAnimator = ValueAnimator.ofArgb(*ints).apply {
                this.duration = duration
                if (listener == null) {
                    addUpdateListener { valueAnimator ->
                        val value = valueAnimator.animatedValue as Int

                        view.setBackgroundColor(value)
                    }
                } else {
                    addUpdateListener(listener)
                    valueAnimator.repeatMode
                }
            }

            if (animatorListener != null)
                valueAnimator.addListener(animatorListener)

            return this
        }

        fun build() : ValueAnimator {
            return valueAnimator
        }
    }

    fun addAnimation(valueAnimator: ValueAnimator) : ValueAnimator {
        animationsList.add(valueAnimator)
        return valueAnimator
    }

    fun reverse() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            animatorSet.reverse()
        } else {
            for (animator in animatorSet.childAnimations) {
                (animator as ValueAnimator).reverse()
            }
        }
    }

    fun playSequentially() {
        animatorSet.playSequentially(animationsList)
        animatorSet.start()
    }

    fun playTogether() {
        animatorSet.playTogether(animationsList)
        animatorSet.start()

    }

    fun isEmpty() : Boolean {
        return animationsList.isEmpty()
    }

    fun stop() {
        animatorSet.cancel()
    }

    companion object {
        private val TAG: String = "ValueAnimatorHelper"
    }
}