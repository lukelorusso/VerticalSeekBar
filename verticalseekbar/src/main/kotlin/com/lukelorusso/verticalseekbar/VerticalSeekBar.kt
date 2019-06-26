@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.lukelorusso.verticalseekbar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.layout_verticalseekbar.view.*
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * A nicer, redesigned and vertical SeekBar
 */
open class VerticalSeekBar constructor(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    companion object {
        const val DEFAULT_DRAWABLE_BACKGROUND: String = "#f6f6f6"
        const val DEFAULT_DRAWABLE_PROGRESS_START: String = "#4D88E1"
        const val DEFAULT_DRAWABLE_PROGRESS_END: String = "#7BA1DB"
    }

    private var onProgressChangeListener: ((Int) -> Unit)? = null

    var clickToSetProgress = true
        set(value) {
            field = value
            applyAttributes()
        }
    var drawableCornerRadius: Int = 0
        set(value) {
            field = value
            applyAttributes()
        }
    var drawableBackgroundDrawable: Drawable? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var drawableProgressDrawable: Drawable? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var drawableProgressStartColor: Int = Color.parseColor(DEFAULT_DRAWABLE_PROGRESS_START)
        set(value) {
            field = value
            applyAttributes()
        }
    var drawableProgressEndColor: Int = Color.parseColor(DEFAULT_DRAWABLE_PROGRESS_END)
        set(value) {
            field = value
            applyAttributes()
        }
    var drawableWidth: Int? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var minLayoutWidth: Int = 0
        set(value) {
            field = value
            applyAttributes()
        }
    var minLayoutHeight: Int = 0
        set(value) {
            field = value
            applyAttributes()
        }
    var maxPlaceholderDrawable: Drawable? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var minPlaceholderDrawable: Drawable? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var showThumb = true
        set(value) {
            field = value
            applyAttributes()
        }
    var thumbContainerColor: Int = Color.WHITE
        set(value) {
            field = value
            applyAttributes()
        }
    var thumbContainerCornerRadius: Int = 0
        set(value) {
            field = value
            applyAttributes()
        }
    var thumbPlaceholderDrawable: Drawable? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var useThumbToSetProgress = true
        set(value) {
            field = value
            applyAttributes()
        }
    var progress: Int = 50
        set(value) {
            if (field != value) {
                onProgressChangeListener?.invoke(value)
            }
            field = value
            updateViews(value)
        }
    private var yDelta: Int = 0
    private var initEnded = false // if true allows the view to be updated after setting an attribute programmatically

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        inflate(context, R.layout.layout_verticalseekbar, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar, 0, 0)
        try {
            clickToSetProgress =
                attributes.getBoolean(R.styleable.VerticalSeekBar_vsb_click_to_set_progress, clickToSetProgress)
            attributes.getLayoutDimension(R.styleable.VerticalSeekBar_android_layout_width, minLayoutWidth).also {
                container.layoutParams.width = if (it != -1 && it < minLayoutWidth) minLayoutWidth // wrap_content
                else it
            }
            attributes.getLayoutDimension(R.styleable.VerticalSeekBar_android_layout_height, minLayoutHeight).also {
                container.layoutParams.height =
                    if (it != -1 && it < minLayoutHeight) minLayoutHeight // wrap_content
                    else it
            }
            attributes.getDrawable(R.styleable.VerticalSeekBar_vsb_drawable_background)?.also {
                drawableBackgroundDrawable = it
            }
            attributes.getDrawable(R.styleable.VerticalSeekBar_vsb_drawable_progress).also {
                drawableProgressDrawable = it
            }
            attributes.getDrawable(R.styleable.VerticalSeekBar_vsb_min_placeholder_src).also {
                minPlaceholderDrawable = it
            }
            attributes.getDrawable(R.styleable.VerticalSeekBar_vsb_max_placeholder_src).also {
                maxPlaceholderDrawable = it
            }
            drawableCornerRadius = attributes.getLayoutDimension(
                R.styleable.VerticalSeekBar_vsb_drawable_corner_radius,
                drawableCornerRadius
            )
            drawableProgressStartColor =
                attributes.getColor(
                    R.styleable.VerticalSeekBar_vsb_drawable_progress_gradient_start,
                    drawableProgressStartColor
                )
            drawableProgressEndColor =
                attributes.getColor(
                    R.styleable.VerticalSeekBar_vsb_drawable_progress_gradient_end,
                    drawableProgressEndColor
                )
            showThumb = attributes.getBoolean(R.styleable.VerticalSeekBar_vsb_show_thumb, showThumb)
            thumbContainerColor =
                attributes.getColor(R.styleable.VerticalSeekBar_vsb_thumb_container_tint, thumbContainerColor)
            thumbContainerCornerRadius = attributes.getLayoutDimension(
                R.styleable.VerticalSeekBar_vsb_thumb_container_corner_radius,
                thumbContainerCornerRadius
            )
            attributes.getDrawable(R.styleable.VerticalSeekBar_vsb_thumb_placeholder_src).also {
                thumbPlaceholderDrawable = it
            }
            drawableWidth = attributes.getDimensionPixelSize(
                R.styleable.VerticalSeekBar_vsb_drawable_width,
                drawableWidth ?: container.layoutParams.width
            )
            attributes.getInt(R.styleable.VerticalSeekBar_vsb_progress, progress).also {
                progress = when {
                    it < 0 -> 0
                    it > 100 -> 100
                    else -> it
                }
            }
            useThumbToSetProgress =
                attributes.getBoolean(R.styleable.VerticalSeekBar_vsb_use_thumb_to_set_progress, useThumbToSetProgress)

        } finally {
            attributes.recycle()
        }

        initEnded = true
        applyAttributes()
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

    private fun applyAttributes() {
        if (initEnded) {
            initEnded = false // will be released at the end

            // Customizing drawableCardView
            drawableCardView.layoutParams.width = drawableWidth ?: 0

            // Customizing drawableBackground
            drawableBackground.background =
                drawableBackgroundDrawable ?: ColorDrawable(Integer.decode(DEFAULT_DRAWABLE_BACKGROUND))

            // Generating drawableProgress gradient
            if (drawableProgressDrawable == null) drawableProgressDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(drawableProgressStartColor, drawableProgressEndColor)
            ).apply { cornerRadius = 0f }
            drawableProgress.background = drawableProgressDrawable

            // Applying card corner radius
            drawableCardView.radius = drawableCornerRadius.toFloat()
            thumbCardView.radius = thumbContainerCornerRadius.toFloat()

            // Applying custom placeholders
            maxPlaceholder.setImageDrawable(maxPlaceholderDrawable) // can also be null
            minPlaceholder.setImageDrawable(minPlaceholderDrawable) // can also be null

            // Let's shape the thumb
            if (showThumb) {
                thumbPlaceholderDrawable?.also { thumbPlaceholder.setImageDrawable(it) } // CANNOT be null
                thumbCardView.visibility = View.VISIBLE
                val states = arrayOf(
                    intArrayOf(android.R.attr.state_enabled), // enabled
                    intArrayOf(-android.R.attr.state_enabled), // disabled
                    intArrayOf(-android.R.attr.state_checked), // unchecked
                    intArrayOf(android.R.attr.state_pressed)  // pressed
                )
                val colors = arrayOf(
                    thumbContainerColor,
                    thumbContainerColor,
                    thumbContainerColor,
                    thumbContainerColor
                ).toIntArray()
                thumbCardView.backgroundTintList = ColorStateList(states, colors)
                thumbCardView.measure(0, 0)
                thumb.layoutParams.apply {
                    val increase = (thumbCardView.elevation + context.dpToPixel(1F)).roundToInt()
                    width = thumbCardView.measuredWidth + increase
                    height = thumbCardView.measuredHeight + increase
                    (thumbCardView.layoutParams as LayoutParams).topMargin = increase / 2
                }
            } else thumbCardView.visibility = View.GONE

            // Adding some margin to drawableCardView, maxPlaceholder and minPlaceholder
            maxPlaceholder.measure(0, 0)
            minPlaceholder.measure(0, 0)
            (drawableCardView.layoutParams as LayoutParams).apply {
                val thumbCardViewHalfHeight = if (showThumb) thumbCardView.measuredHeight / 2 else 0
                val maxPlaceholderHalfHeight = maxPlaceholder.measuredHeight / 2
                val minPlaceholderHalfHeight = minPlaceholder.measuredHeight / 2
                topMargin = max(thumbCardViewHalfHeight, maxPlaceholderHalfHeight)
                bottomMargin = max(thumbCardViewHalfHeight, minPlaceholderHalfHeight)
                (maxPlaceholder.layoutParams as LayoutParams).topMargin = topMargin - maxPlaceholderHalfHeight
                (maxPlaceholder.layoutParams as LayoutParams).bottomMargin = topMargin - maxPlaceholderHalfHeight
                (minPlaceholder.layoutParams as LayoutParams).bottomMargin = bottomMargin - minPlaceholderHalfHeight
                (minPlaceholder.layoutParams as LayoutParams).topMargin = bottomMargin - minPlaceholderHalfHeight

            }

            // Here's where the magic happens
            if (showThumb && useThumbToSetProgress) thumb.setOnTouchListener { thumb, event ->
                val rawY = event.rawY.roundToInt()
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN -> { // here we calculate the displacement (yDelta)
                        yDelta = rawY - (thumb.layoutParams as LayoutParams).topMargin
                    }
                    MotionEvent.ACTION_MOVE -> { // here we update progress
                        val positionY = rawY - yDelta
                        val fillHeight = height - thumb.height
                        when {
                            positionY in 1 until fillHeight -> progress = 100 - (positionY * 100 / fillHeight)
                            positionY <= 0 -> progress = 100
                            positionY >= fillHeight -> progress = 0
                        }
                    }
                }
                true
            } else thumb.setOnTouchListener(null)

            // here we intercept the click on the drawable
            if (clickToSetProgress) drawableCardView.setOnTouchListener { drawable, event ->
                val positionY = event.y.roundToInt()
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        val fillHeight = drawable.measuredHeight
                        when {
                            positionY in 1 until fillHeight -> progress = 100 - (positionY * 100 / fillHeight)
                            positionY <= 0 -> progress = 100
                            positionY >= fillHeight -> progress = 0
                        }
                    }
                }
                true
            } else drawableCardView.setOnTouchListener(null)

            initEnded = true
        }
    }

    /**
     * Inside here the views are repositioned based on the new value
     */
    private fun updateViews(value: Int) {
        post {
            val fillHeight = height - thumb.height
            val marginByProgress = fillHeight - (value * fillHeight / 100)
            thumb.layoutParams =
                (thumb.layoutParams as LayoutParams).apply { topMargin = marginByProgress }
            drawableProgress.translationY = (drawableBackground.height * (100 - value) / 100).toFloat()
            invalidate()
        }
    }

}
