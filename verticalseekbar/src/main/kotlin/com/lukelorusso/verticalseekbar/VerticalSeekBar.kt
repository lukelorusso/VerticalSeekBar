@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.lukelorusso.verticalseekbar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.layout_verticalseekbar.view.*

/**
 * Simple View to draw lines, circles or text labels
 */
open class VerticalSeekBar constructor(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private var onProgressChangeListener: ((Int) -> Unit)? = null

    private var yDelta: Int = 0
    private var minLayoutWidth = 0
    private var minLayoutHeight = 0
    var progressValue: Int = 50
        set(value) {
            if (field != value) {
                onProgressChangeListener?.invoke(value)
            }
            field = value
            updateValue()
        }

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        inflate(context, R.layout.layout_verticalseekbar, this)

        /*minLayoutWidth = thumb.layoutParams.width
        minLayoutHeight = thumb.layoutParams.height + 1*/

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar, 0, 0)
        attributes.getLayoutDimension(R.styleable.VerticalSeekBar_android_layout_width, minLayoutWidth).apply {
            container.layoutParams.width = if (this != -1 && this < minLayoutWidth) minLayoutWidth // wrap_content
            else this
        }
        attributes.getLayoutDimension(R.styleable.VerticalSeekBar_android_layout_height, minLayoutHeight).apply {
            container.layoutParams.height = if (this != -1 && this < minLayoutHeight) minLayoutHeight // wrap_content
            else this
        }
        val startColor = attributes.getColor(R.styleable.VerticalSeekBar_vsb_gradient_start, Color.BLACK)
        val endColor = attributes.getColor(R.styleable.VerticalSeekBar_vsb_gradient_end, Color.BLACK)
        attributes.getInt(R.styleable.VerticalSeekBar_vsb_progress, progressValue).also {
            progressValue = when {
                it < 0 -> 0
                it > 100 -> 100
                else -> it
            }
        }
        attributes.recycle()

        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(startColor, endColor)
        ).apply { cornerRadius = 0f }

        progress.background = gradientDrawable

        thumb.setOnTouchListener { thumbView, event ->
            val rawY = event.rawY.toInt()
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    yDelta = rawY - (thumbView.layoutParams as LayoutParams).topMargin
                }
                MotionEvent.ACTION_MOVE -> {
                    val positionY = rawY - yDelta
                    val fillHeight = height - thumbView.height
                    when {
                        positionY in 1 until fillHeight -> progressValue = 100 - (positionY * 100 / fillHeight)
                        positionY <= 0 -> progressValue = 100
                        positionY >= fillHeight -> progressValue = 0
                    }
                }
            }
            true
        }
    }

    fun setOnProgressChangeListener(listener: ((Int) -> Unit)?) {
        this.onProgressChangeListener = listener
    }

    //region PROTECTED METHODS
    protected fun Context.dpToPixel(dp: Float): Float =
        dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

    protected fun Context.pixelToDp(px: Float): Float =
        px / (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    //endregion

    private fun updateValue() {
        post {
            val fillHeight = height - thumb.height
            val marginByProgress = fillHeight - (progressValue * fillHeight / 100)
            thumb.layoutParams =
                (thumb.layoutParams as LayoutParams).apply { topMargin = marginByProgress }
            progress.translationY = marginByProgress.toFloat()
            invalidate()
        }
    }

}
