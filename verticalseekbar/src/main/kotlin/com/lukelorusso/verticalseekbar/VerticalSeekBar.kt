@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.lukelorusso.verticalseekbar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.layout_verticalseekbar.view.*
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * A nicer, redesigned and vertical SeekBar
 */
open class VerticalSeekBar constructor(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    companion object {
        private const val DEFAULT_MAX_VALUE = 100
        private const val DEFAULT_DRAWABLE_BACKGROUND: String = "#f6f6f6"
        private const val DEFAULT_DRAWABLE_PROGRESS_START: String = "#4D88E1"
        private const val DEFAULT_DRAWABLE_PROGRESS_END: String = "#7BA1DB"
    }

    enum class Placeholder {
        OUTSIDE,
        INSIDE,
        MIDDLE
    }

    private var onProgressChangeListener: ((Int) -> Unit)? = null

    var clickToSetProgress = true
        set(value) {
            field = value
            applyAttributes()
        }
    var barCornerRadius: Int = 0
        set(value) {
            field = value
            applyAttributes()
        }
    var barBackgroundDrawable: Drawable? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var barBackgroundStartColor: Int = Color.parseColor(DEFAULT_DRAWABLE_BACKGROUND)
        set(value) {
            field = value
            barBackgroundDrawable = null
            applyAttributes()
        }
    var barBackgroundEndColor: Int = Color.parseColor(DEFAULT_DRAWABLE_BACKGROUND)
        set(value) {
            field = value
            barBackgroundDrawable = null
            applyAttributes()
        }
    var barProgressDrawable: Drawable? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var barProgressStartColor: Int = Color.parseColor(DEFAULT_DRAWABLE_PROGRESS_START)
        set(value) {
            field = value
            barProgressDrawable = null
            applyAttributes()
        }
    var barProgressEndColor: Int = Color.parseColor(DEFAULT_DRAWABLE_PROGRESS_END)
        set(value) {
            field = value
            barProgressDrawable = null
            applyAttributes()
        }
    var barWidth: Int? = null
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
    private var maxPlaceholderPosition = Placeholder.MIDDLE
    var minPlaceholderDrawable: Drawable? = null
        set(value) {
            field = value
            applyAttributes()
        }
    private var minPlaceholderPosition = Placeholder.MIDDLE
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
    var maxValue = DEFAULT_MAX_VALUE
        set(value) {
            val newValue = when {
                value < 1 -> 1
                else -> value
            }
            if (progress > newValue) progress = newValue
            field = newValue
            updateViews()
        }
    var progress: Int = 50
        set(value) {
            val newValue = when {
                value < 0 -> 0
                value > maxValue -> maxValue
                else -> value
            }
            if (field != newValue) {
                onProgressChangeListener?.invoke(newValue)
            }
            field = newValue
            updateViews()
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
            barCornerRadius = attributes.getLayoutDimension(
                R.styleable.VerticalSeekBar_vsb_bar_corner_radius,
                barCornerRadius
            )
            barBackgroundStartColor =
                attributes.getColor(
                    R.styleable.VerticalSeekBar_vsb_bar_background_gradient_start,
                    barBackgroundStartColor
                )
            barBackgroundEndColor =
                attributes.getColor(
                    R.styleable.VerticalSeekBar_vsb_bar_background_gradient_end,
                    barBackgroundEndColor
                )
            attributes.getDrawable(R.styleable.VerticalSeekBar_vsb_bar_background)?.also {
                barBackgroundDrawable = it
            }
            barProgressStartColor =
                attributes.getColor(
                    R.styleable.VerticalSeekBar_vsb_bar_progress_gradient_start,
                    barProgressStartColor
                )
            barProgressEndColor =
                attributes.getColor(
                    R.styleable.VerticalSeekBar_vsb_bar_progress_gradient_end,
                    barProgressEndColor
                )
            attributes.getDrawable(R.styleable.VerticalSeekBar_vsb_bar_progress).also {
                barProgressDrawable = it
            }
            barWidth = attributes.getDimensionPixelSize(
                R.styleable.VerticalSeekBar_vsb_bar_width,
                barWidth ?: container.layoutParams.width
            )
            attributes.getLayoutDimension(R.styleable.VerticalSeekBar_android_layout_width, minLayoutWidth).also {
                container.layoutParams.width = if (it != -1 && it < minLayoutWidth) minLayoutWidth // wrap_content
                else it
            }
            attributes.getLayoutDimension(R.styleable.VerticalSeekBar_android_layout_height, minLayoutHeight).also {
                container.layoutParams.height =
                    if (it != -1 && it < minLayoutHeight) minLayoutHeight // wrap_content
                    else it
            }
            attributes.getDrawable(R.styleable.VerticalSeekBar_vsb_max_placeholder_src).also {
                maxPlaceholderDrawable = it
            }
            maxPlaceholderPosition = Placeholder.values()[attributes.getInt(
                R.styleable.VerticalSeekBar_vsb_max_placeholder_position,
                maxPlaceholderPosition.ordinal
            )]
            attributes.getDrawable(R.styleable.VerticalSeekBar_vsb_min_placeholder_src).also {
                minPlaceholderDrawable = it
            }
            minPlaceholderPosition = Placeholder.values()[attributes.getInt(
                R.styleable.VerticalSeekBar_vsb_min_placeholder_position,
                minPlaceholderPosition.ordinal
            )]
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
            attributes.getInt(R.styleable.VerticalSeekBar_vsb_progress, progress).also {
                progress = when {
                    it < 0 -> 0
                    it > maxValue -> maxValue
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
            barCardView.layoutParams.width = barWidth ?: 0

            // Customizing drawableBackground
            if (barBackgroundDrawable == null) barBackgroundDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(barBackgroundStartColor, barBackgroundEndColor)
            ).apply { cornerRadius = 0f }
            barBackground.background = barBackgroundDrawable

            // Customizing drawableProgress
            if (barProgressDrawable == null) barProgressDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(barProgressStartColor, barProgressEndColor)
            ).apply { cornerRadius = 0f }
            barProgress.background = barProgressDrawable

            // Applying card corner radius
            barCardView.radius = barCornerRadius.toFloat()
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

                ViewCompat.setBackgroundTintList(thumbCardView, ColorStateList(states, colors))
                thumbCardView.measure(0, 0)
                thumb.layoutParams.apply {
                    val increase = (ViewCompat.getElevation(thumbCardView) + context.dpToPixel(1F)).roundToInt()
                    width = thumbCardView.measuredWidth + increase
                    height = thumbCardView.measuredHeight + increase
                    (thumbCardView.layoutParams as LayoutParams).topMargin = increase / 2
                }
            } else thumbCardView.visibility = View.GONE

            // Adding some margin to drawableCardView, maxPlaceholder and minPlaceholder
            val maxPlaceholderLayoutParams = (maxPlaceholder.layoutParams as LayoutParams)
            val minPlaceholderLayoutParams = (minPlaceholder.layoutParams as LayoutParams)
            maxPlaceholder.measure(0, 0)
            minPlaceholder.measure(0, 0)
            (barCardView.layoutParams as LayoutParams).apply {
                val thumbCardViewHalfHeight = if (showThumb) thumbCardView.measuredHeight / 2 else 0

                val maxPlaceholderHalfHeight = maxPlaceholder.measuredHeight / 2
                when (maxPlaceholderPosition) {
                    Placeholder.INSIDE -> {
                        topMargin = thumbCardViewHalfHeight
                        maxPlaceholderLayoutParams.topMargin = topMargin
                    }
                    Placeholder.OUTSIDE -> {
                        topMargin = maxPlaceholder.measuredHeight +
                                if (thumbCardViewHalfHeight > maxPlaceholder.measuredHeight)
                                    thumbCardViewHalfHeight - maxPlaceholder.measuredHeight
                                else 0
                        maxPlaceholderLayoutParams.topMargin = topMargin - maxPlaceholder.measuredHeight
                    }
                    else -> {
                        topMargin = max(thumbCardViewHalfHeight, maxPlaceholderHalfHeight)
                        maxPlaceholderLayoutParams.topMargin = topMargin - maxPlaceholderHalfHeight
                    }
                }
                maxPlaceholderLayoutParams.bottomMargin = maxPlaceholderLayoutParams.topMargin

                val minPlaceholderHalfHeight = minPlaceholder.measuredHeight / 2
                when (minPlaceholderPosition) {
                    Placeholder.INSIDE -> {
                        bottomMargin = thumbCardViewHalfHeight
                        minPlaceholderLayoutParams.bottomMargin = bottomMargin
                    }
                    Placeholder.OUTSIDE -> {
                        bottomMargin = minPlaceholder.measuredHeight +
                                if (thumbCardViewHalfHeight > minPlaceholder.measuredHeight)
                                    thumbCardViewHalfHeight - minPlaceholder.measuredHeight
                                else 0
                        minPlaceholderLayoutParams.bottomMargin = bottomMargin - minPlaceholder.measuredHeight
                    }
                    else -> {
                        bottomMargin = max(thumbCardViewHalfHeight, minPlaceholderHalfHeight)
                        minPlaceholderLayoutParams.bottomMargin = bottomMargin - minPlaceholderHalfHeight
                    }
                }
                minPlaceholderLayoutParams.topMargin = minPlaceholderLayoutParams.bottomMargin

            }

            // here we intercept the click on the thumb
            if (showThumb && useThumbToSetProgress) thumb.setOnTouchListener { thumb, event ->
                val rawY = event.rawY.roundToInt()
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN -> { // here we get the max top y coordinate (yDelta)
                        yDelta = rawY - (thumb.layoutParams as LayoutParams).topMargin
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val positionY = rawY - yDelta // here we calculate the displacement
                        val fillHeight = height - thumb.height
                        when { // here we update progress
                            positionY in 1 until fillHeight -> {
                                val newValue = maxValue - (positionY.toFloat() * maxValue / fillHeight)
                                progress = newValue.roundToInt()
                            }
                            positionY <= 0 -> progress = maxValue
                            positionY >= fillHeight -> progress = 0
                        }
                    }
                }
                true
            } else thumb.setOnTouchListener(null)

            // here we intercept the click on the bar
            if (clickToSetProgress) barCardView.setOnTouchListener { drawable, event ->
                val positionY = event.y.roundToInt()
                val action = {
                    val fillHeight = drawable.measuredHeight
                    when { // here we update progress
                        positionY in 1 until fillHeight -> {
                            val newValue = maxValue - (positionY.toFloat() * maxValue / fillHeight)
                            progress = newValue.roundToInt()
                        }
                        positionY <= 0 -> progress = maxValue
                        positionY >= fillHeight -> progress = 0
                    }
                }
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN -> action.invoke()
                    MotionEvent.ACTION_MOVE -> if (useThumbToSetProgress) action.invoke()
                }
                true
            } else barCardView.setOnTouchListener(null)

            initEnded = true
        }
    }

    /**
     * Inside here the views are repositioned based on the new value
     */
    private fun updateViews() {
        post {
            val barCardViewLayoutParams = barCardView.layoutParams as LayoutParams
            val fillHeight = height - barCardViewLayoutParams.topMargin - barCardViewLayoutParams.bottomMargin
            val marginByProgress = fillHeight - (progress * fillHeight / maxValue)
            thumb.layoutParams =
                (thumb.layoutParams as LayoutParams).apply { topMargin = marginByProgress }
            barProgress.translationY = (barBackground.height * (maxValue - progress) / maxValue).toFloat()
            invalidate()
        }
    }

}
