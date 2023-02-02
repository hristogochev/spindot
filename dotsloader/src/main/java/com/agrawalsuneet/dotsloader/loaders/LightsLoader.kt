package com.agrawalsuneet.dotsloader.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.LinearLayout
import com.agrawalsuneet.dotsloader.R
import com.agrawalsuneet.dotsloader.basicviews.CircleView
import com.agrawalsuneet.dotsloader.contracts.AnimationContract
import com.agrawalsuneet.dotsloader.utils.getColorResource
import com.agrawalsuneet.dotsloader.utils.random
import java.util.ArrayList

class LightsLoader : LinearLayout,AnimationContract {

    // Default input attributes
    private val defaultNoOfCircles = 3
    private val defaultCircleRadius = 30
    private val defaultCircleDistance = 10
    private val defaultCircleColor = getColorResource(android.R.color.holo_purple)
    private val defaultToggleOnVisibilityChange = true

    // Settable attributes
    private var noOfCircles = defaultNoOfCircles
        set(value) {
            field = if (value < 1) 1 else value
        }
    private var circleRadius = defaultCircleRadius
    private var circleDistance = defaultCircleDistance
    private var circleColor = defaultCircleColor
    private var toggleOnVisibilityChange = defaultToggleOnVisibilityChange

    // Circles
    private var calWidthHeight: Int = 0
    private var circlesList = ArrayList<CircleView>()

    // Custom constructors
    constructor(
        context: Context,
        noOfCircles: Int,
        circleRadius: Int,
        circleDistance: Int,
        circleColor: Int,
        toggleOnVisibilityChange: Boolean
    ) : super(context) {
        this.noOfCircles = noOfCircles
        this.circleRadius = circleRadius
        this.circleDistance = circleDistance
        this.circleColor = circleColor
        this.toggleOnVisibilityChange = toggleOnVisibilityChange
        initViews()
    }

    // Default constructors
    constructor(context: Context) : super(context) {
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttributes(attrs)
        initViews()
    }


    private fun initAttributes(attrs: AttributeSet) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LightsLoader, 0, 0)

        noOfCircles =
            typedArray.getInteger(R.styleable.LightsLoader_lights_noOfCircles, defaultNoOfCircles)

        circleRadius =
            typedArray.getDimensionPixelSize(
                R.styleable.LightsLoader_lights_circleRadius,
                defaultCircleRadius
            )
        circleDistance =
            typedArray.getDimensionPixelSize(
                R.styleable.LightsLoader_lights_circleDistance,
                defaultCircleDistance
            )

        circleColor = typedArray.getColor(
            R.styleable.LightsLoader_lights_circleColor,
            defaultCircleColor
        )

        toggleOnVisibilityChange = typedArray.getBoolean(
            R.styleable.LightsLoader_lights_toggleOnVisibilityChange,
            defaultToggleOnVisibilityChange
        )

        typedArray.recycle()
    }

    private fun initViews() {
        this.orientation = VERTICAL

        if (calWidthHeight == 0) {
            calWidthHeight = (2 * circleRadius * noOfCircles) + ((noOfCircles - 1) * circleDistance)
        }

        for (countI in 0 until noOfCircles) {
            val linearLayout = LinearLayout(context).apply {
                orientation = HORIZONTAL
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
                ).apply {
                    if (countI != 0) topMargin = circleDistance
                }
            }

            for (countJ in 0 until noOfCircles) {
                val circleView = CircleView(context, circleRadius, circleColor)

                val innerParam = LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
                ).apply {
                    if (countJ != 0) leftMargin = circleDistance
                }

                linearLayout.addView(circleView, innerParam)
                circlesList.add(circleView)
            }

            addView(linearLayout)
        }
    }

    override fun startAnimation() {
        // Clear previous animations
        clearPreviousAnimations()

        for (count in 0 until noOfCircles) {
            for (item in circlesList) {
                item.startAnimation(getAlphaAnimation())
            }
        }
    }

    override fun stopAnimation() {
        clearPreviousAnimations()
    }

    override fun clearPreviousAnimations() {
        for (count in 0 until noOfCircles) {
            for (item in circlesList) {
                item.clearAnimation()
            }
        }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (calWidthHeight == 0) {
            calWidthHeight = (2 * circleRadius * noOfCircles) + ((noOfCircles - 1) * circleDistance)
        }

        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (!toggleOnVisibilityChange) return

        if (visibility != VISIBLE) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }

    // Utility functions
    private fun getAlphaAnimation(): Animation {
        val fromAlpha = (0.5f..1.0f).random()
        val toAlpha = (0.1f..0.5f).random()

        return AlphaAnimation(fromAlpha, toAlpha)
            .apply {
                duration = (100..1000).random().toLong()
                repeatMode = Animation.REVERSE
                repeatCount = Animation.INFINITE
            }
    }
}

