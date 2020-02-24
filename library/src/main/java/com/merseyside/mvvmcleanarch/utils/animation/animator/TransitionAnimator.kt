package com.merseyside.mvvmcleanarch.utils.animation.animator

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import com.merseyside.mvvmcleanarch.utils.Logger
import com.merseyside.mvvmcleanarch.utils.animation.*
import com.merseyside.mvvmcleanarch.utils.time.TimeUnit

class TransitionAnimator (
    builder: Builder
): BaseSingleAnimator(builder) {

    class Builder(
        view: View,
        duration: TimeUnit
    ): BaseAnimatorBuilder<TransitionAnimator>(view, duration) {

        private var pointList: List<Pair<Float, MainPoint>>? = null
        private var animAxis: AnimAxis? = null

        fun setInPercents(pointPercents: List<Pair<Float, MainPoint>>, animAxis: AnimAxis) {
            this.animAxis = animAxis

            pointList = getPixelsFromPercents(pointPercents)
        }

        fun setInPixels(pointPixels: List<Pair<Float, MainPoint>>, animAxis: AnimAxis) {
            this.animAxis = animAxis

            pointList = pointPixels
        }

        private fun getPixelsFromPercents(
            pointPercents: List<Pair<Float, MainPoint>>
        ): List<Pair<Float, MainPoint>> {

            val viewSize = when (animAxis!!) {
                AnimAxis.X_AXIS -> {
                    (view.parent as View).width
                }
                AnimAxis.Y_AXIS -> {
                    (view.parent as View).height
                }
            }

            return pointPercents.toMutableList().let { list ->
                var i = 0

                while (i < pointPercents.size) {

                    list[i] = (viewSize * list[i].first) to list[i].second
                    i++
                }

                list
            }
        }

        private fun translateAnimation(
            pointFloats: List<Pair<Float, MainPoint>>,
            animAxis: AnimAxis,
            duration: TimeUnit,
            isLogValues: Boolean = false
        ) : Animator {

            val floatArray = pointFloats.toMutableList().let { list ->

                pointFloats.forEachIndexed { index, value ->
                    if (value == getCurrentValue()) {
                        list[index] = calculateCurrentValue()
                    }
                }

                if (pointFloats.size == 1) {

                    list.add(calculateCurrentValue())

                    pointList = list
                }

                recalculateValues(list, animAxis).also {
                    if (isReverse) it.reverse()
                }
            }

            if (isLogValues) {
                Logger.log(this, floatArray.joinToString())
            }

            return ValueAnimator.ofFloat(*floatArray).apply {
                this.duration = duration.toMillisLong()

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
            }
        }

        private fun recalculateValues(values: MutableList<Pair<Float, MainPoint>>, animAxis: AnimAxis) : FloatArray {

            val viewSize = when (animAxis) {
                AnimAxis.X_AXIS -> {
                    view.width
                }
                AnimAxis.Y_AXIS -> {
                    view.height
                }
            }

            val floatArray = FloatArray(values.size)

            var i = 0
            while (i < values.size) {
                val value = values[i].first

                when (values[i].second) {

                    MainPoint.CENTER -> {
                        floatArray[i] = value - viewSize / 2
                    }

                    MainPoint.TOP_RIGHT -> {
                        when (animAxis) {
                            AnimAxis.X_AXIS ->
                                floatArray[i] = value + viewSize
                            AnimAxis.Y_AXIS -> {}
                        }
                    }

                    MainPoint.BOTTOM_LEFT -> {
                        when (animAxis) {
                            AnimAxis.X_AXIS -> {}

                            AnimAxis.Y_AXIS -> {
                                floatArray[i] = value - viewSize
                            }
                        }
                    }

                    MainPoint.BOTTOM_RIGHT -> {
                        when (animAxis) {
                            AnimAxis.X_AXIS -> {
                                floatArray[i] = value - viewSize
                            }
                            AnimAxis.Y_AXIS -> {
                                floatArray[i] = value - viewSize
                            }
                        }
                    }

                    else -> {
                        floatArray[i] = value
                    }
                }

                i++
            }

            return floatArray
        }

        override fun getCurrentValue(): Pair<Float, MainPoint> {
            return CURRENT_FLOAT to MainPoint.TOP_LEFT
        }

        private fun calculateCurrentValue(): Pair<Float, MainPoint> {
            return when (animAxis) {
                AnimAxis.Y_AXIS ->
                    view.y to MainPoint.TOP_LEFT
                AnimAxis.X_AXIS ->
                    view.x to MainPoint.TOP_LEFT
                null -> throw NullPointerException()
            }
        }

        @Throws(IllegalArgumentException::class)
        override fun build(): Animator {
            if (pointList != null && animAxis != null) {
                return translateAnimation(pointList!!, animAxis!!, duration)
            } else {
                throw IllegalArgumentException("Points haven't been set")
            }
        }
    }
}