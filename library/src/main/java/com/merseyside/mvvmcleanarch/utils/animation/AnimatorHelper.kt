package com.merseyside.mvvmcleanarch.utils.animation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.animation.AnimationSet
import com.merseyside.mvvmcleanarch.utils.Logger
import com.merseyside.mvvmcleanarch.utils.time.TimeUnit


class AnimatorHelper {

    private val mainAnimatorSet: AnimatorSet by lazy {
        initCallbacks()

        AnimatorSet().apply {
            addListener(internalListener)
        }
    }

    var isReverseAllowed: Boolean = false

    private var onEndCallback: (animation: Animator?) -> Unit? = {}
    private var onRepeatCallback: (animation: Animator?) -> Unit? = {}
    private var onCancelCallback: (animation: Animator?) -> Unit? = {}
    private var onStartCallback: (animation: Animator?) -> Unit? = {}

    var isEmpty: Boolean = mainAnimatorSet.childAnimations.size != 0

    private var internalListener: Animator.AnimatorListener? = null

    private fun initCallbacks() {
        internalListener = object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                onRepeatCallback.invoke(animation)
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEndCallback.invoke(animation)
            }

            override fun onAnimationCancel(animation: Animator?) {
                onCancelCallback.invoke(animation)
            }

            override fun onAnimationStart(animation: Animator?) {
                onStartCallback.invoke(animation)
            }
        }
    }

    class Builder(val view: View, var isReverse: Boolean = false) {

        lateinit var valueAnimator: ValueAnimator
            private set

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

        fun translateAnimationPercent(
            pointPercents: List<Pair<Float, MainPoint>>,
            animAxis: AnimAxis,
            duration: TimeUnit,
            updateListener : ValueAnimator.AnimatorUpdateListener? = null,
            animatorListener: Animator.AnimatorListener? = null,
            isLogValues: Boolean = false
        ) : Builder {

            val viewSize = when (animAxis) {
                AnimAxis.X_AXIS -> {
                    (view.parent as View).width
                }
                AnimAxis.Y_AXIS -> {
                    (view.parent as View).height
                }
            }

            pointPercents.toMutableList().let { list ->
                var i = 0

                while (i < pointPercents.size) {

                    list[i] = (viewSize * list[i].first) to list[i].second
                    i++
                }

                return translateAnimation(list, animAxis = animAxis,
                    duration = duration, listener = updateListener, animatorListener = animatorListener, isLogValues = isLogValues)
            }
        }

        fun translateAnimation(
            pointFloats: List<Pair<Float, MainPoint>>,
            animAxis: AnimAxis,
            duration: TimeUnit, listener : ValueAnimator.AnimatorUpdateListener? = null,
            animatorListener: Animator.AnimatorListener? = null,
            isLogValues: Boolean = false
        ) : Builder {

            val floatArray = pointFloats.toMutableList().let { list ->
                if (pointFloats[0].first == CURRENT_VALUE) {

                    when (animAxis) {
                        AnimAxis.Y_AXIS ->
                            list[0] = view.y to MainPoint.TOP_LEFT
                        AnimAxis.X_AXIS ->
                            list[0] = view.x to MainPoint.TOP_LEFT
                    }

                } else if (pointFloats.size == 1) {

                    when (animAxis) {
                        AnimAxis.Y_AXIS ->
                            list.add(0, view.y to MainPoint.TOP_LEFT)
                        AnimAxis.X_AXIS ->
                            list.add(0, view.x to MainPoint.TOP_LEFT)
                    }
                }

                recalculateValues(list, animAxis).also { if (isReverse) it.reverse() }
            }

            if (isLogValues) {
                Logger.log(this, floatArray.joinToString())
            }

            valueAnimator = ValueAnimator.ofFloat(*floatArray).apply {
                this.duration = duration.toMillisLong()
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

            if (animatorListener != null) {
                valueAnimator.addListener(animatorListener)
            }

            return this
        }

        fun changeSizeAnimationPercent(
            vararg percents: Float,
            animAxis: AnimAxis,
            duration: TimeUnit,
            listener : ValueAnimator.AnimatorUpdateListener? = null,
            animatorListener: Animator.AnimatorListener? = null
        ) : Builder {

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

            return changeSizeAnimation(*newValues, animAxis = animAxis, duration = duration, listener = listener, animatorListener = animatorListener)
        }

        fun changeSizeAnimation(
            vararg floats: Float,
            animAxis: AnimAxis,
            duration: TimeUnit,
            listener : ValueAnimator.AnimatorUpdateListener? = null,
            animatorListener: Animator.AnimatorListener? = null
        ) : Builder {

            val array = floats.toMutableList()

            if (floats[0] == CURRENT_VALUE) {
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
            }

            array.also { if (isReverse) it.reverse() }

            valueAnimator = ValueAnimator.ofFloat(*array.toFloatArray()).apply {
                this.duration = duration.toMillisLong()
                if (listener == null) {
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

            if (animatorListener != null) {
                valueAnimator.addListener(animatorListener)
            }

            return this
        }

        fun scaleAnimation(
            vararg floats: Float,
            animAxis: AnimAxis,
            duration: TimeUnit,
            listener : ValueAnimator.AnimatorUpdateListener? = null,
            animatorListener: Animator.AnimatorListener? = null
        ) : Builder {

            val values = when {
                floats[0] == CURRENT_VALUE -> {
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

                    list.toFloatArray()
                }

                else -> {
                    floats
                }
            }

            values.also { if (isReverse) it.reverse() }

            valueAnimator = ValueAnimator.ofFloat(*values).apply {
                this.duration = duration.toMillisLong()
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

            if (animatorListener != null) {
                valueAnimator.addListener(animatorListener)
            }

            return this
        }

        fun alphaAnimation(
            vararg values: Float,
            duration: TimeUnit,
            listener : ValueAnimator.AnimatorUpdateListener? = null,
            animatorListener: Animator.AnimatorListener? = null
        ): Builder {

            val values = when {
                values[0] == CURRENT_VALUE -> {
                    values[0] = view.alpha
                    values
                }

                values.size == 1 -> {
                    val list = values.toMutableList().apply {
                        add(0, view.alpha)
                    }

                    list.toFloatArray()
                }

                else -> {
                    values
                }
            }

            values.also { if (isReverse) it.reverse() }

            valueAnimator = ValueAnimator.ofFloat(*values).apply {
                this.duration = duration.toMillisLong()

                var previousValue: Float? = values[0]

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
        fun rgbsAnimation(
            vararg ints: Int,
            duration: TimeUnit,
            listener : ValueAnimator.AnimatorUpdateListener? = null,
            animatorListener: Animator.AnimatorListener? = null
        ): Builder {

            val values = if (ints.size == 1) {
                ints.toMutableList().apply {
                    val background: Drawable = view.background
                    if (background is ColorDrawable) {
                        add(0, background.color)
                    }
                }.toIntArray()
            } else {
                ints
            }

            values.also { if (isReverse) it.reverse() }

            valueAnimator = ValueAnimator.ofArgb(*values).apply {
                this.duration = duration.toMillisLong()
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

    @Throws(UnsupportedOperationException::class)
    fun reverse() {
        if (isReverseAllowed) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mainAnimatorSet.reverse()
            } else {
                legacyReverse(mainAnimatorSet)
            }
        } else {
            throw UnsupportedOperationException()
        }
    }

    private fun legacyReverse(rootAnimator: Animator) {

        if (rootAnimator is AnimatorSet) {
            for (animator in rootAnimator.childAnimations) {
                legacyReverse(animator)
            }
        } else {
            if (rootAnimator is ValueAnimator) {
                rootAnimator.reverse()
            }
        }
    }

    fun addAnimatorList(animatorList: AnimatorList, listener: Animator.AnimatorListener? = null) {
        val animatorSet = animatorList.getAnimator()

        when (animatorList.approach) {
            Approach.SEQUENTIALLY -> addSequentialAnimation(animatorSet, listener)
            Approach.TOGETHER -> addTogetherAnimation(animatorSet, listener)
        }
    }

    fun addSequentialAnimation(animator: Animator, listener: Animator.AnimatorListener? = null) {
        if (listener != null) {
            animator.addListener(listener)
        }

        mainAnimatorSet.playSequentially(animator)
    }

    fun addTogetherAnimation(animatorSet: Animator, listener: Animator.AnimatorListener? = null) {
        if (listener != null) {
            animatorSet.addListener(listener)
        }

        mainAnimatorSet.playTogether(animatorSet)
    }


    fun start() {
        if (isRunning()) {
            stop()
        }

        mainAnimatorSet.apply {
            start()
            isReverseAllowed = true
        }
    }

    fun setDelay(delay: Long) {
        mainAnimatorSet.startDelay = delay
    }

    fun isRunning(): Boolean {
        return mainAnimatorSet.isRunning
    }

    fun stop() {
        mainAnimatorSet.cancel()
    }

    fun addListener(listener: Animator.AnimatorListener? = null) {
        mainAnimatorSet.addListener(listener)
    }

    fun removeListener(listener: Animator.AnimatorListener? = null) {
        mainAnimatorSet.removeListener(listener)
    }

    fun setOnEndCallback(onEnd: (animation: Animator?) -> Unit) {
        this.onEndCallback = onEnd
    }

    fun setOnStartCallback(onStart: (animation: Animator?) -> Unit) {
        this.onStartCallback = onStart
    }

    fun setOnRepeatCallback(onRepeat: (animation: Animator?) -> Unit) {
        this.onRepeatCallback = onRepeat
    }

    fun setOnCancelCallback(onCancel: (animation: Animator?) -> Unit) {
        this.onCancelCallback = onCancel
    }

    fun removeAllListeners() {
        mainAnimatorSet.removeAllListeners()
    }

    fun removeAllCallbacks() {
        onEndCallback = {}
        onRepeatCallback  = {}
        onCancelCallback = {}
        onStartCallback = {}
    }

    fun isNotEmpty(): Boolean = !isEmpty

    companion object {
        const val CURRENT_VALUE = 9999F
    }
}