package com.hristogochev.dotloaders.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.LinearLayout
import com.hristogochev.dotloaders.R
import com.hristogochev.dotloaders.animation.AnimationLayout
import com.hristogochev.dotloaders.basicviews.DotView
import com.hristogochev.dotloaders.utils.getColorResource
import com.hristogochev.dotloaders.utils.random
import java.util.ArrayList

/**
 * Modified by hristogochev on 02/02/23.
 */
class LightsLoader : AnimationLayout {

    // Default input attributes
    private val defaultSize = 3
    private val defaultDotRadius = 30f
    private val defaultSpacing = 10
    private val defaultDotColor = getColorResource(android.R.color.holo_purple)

    // Settable attributes
    private var size = defaultSize
        set(value) {
            field = if (value < 1) 1 else value
        }
    private var dotRadius = defaultDotRadius
    private var spacing = defaultSpacing
    private var dotColor = defaultDotColor

    // Circles
    private var calWidthHeight: Int = 0
    private var dotsList = ArrayList<DotView>()

    // General
    private var dotDiameter = (2 * dotRadius).toInt()

    // Custom constructors
    constructor(
        context: Context,
        size: Int? = null,
        dotRadius: Float? = null,
        spacing: Int? = null,
        dotColor: Int? = null,
        toggleOnVisibilityChange: Boolean? = null
    ) : super(context) {
        this.size = size ?: defaultSize
        this.dotRadius = dotRadius ?: defaultDotRadius
        this.spacing = spacing ?: defaultSpacing
        this.dotColor = dotColor ?: defaultDotColor
        this.toggleOnVisibilityChange = toggleOnVisibilityChange ?: defaultToggleOnVisibilityChange
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


    override fun initAttributes(attrs: AttributeSet) {
        super.initAttributes(attrs)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LightsLoader, 0, 0)

        try {
            with(typedArray) {
                size =
                    getInteger(R.styleable.LightsLoader_lights_size, defaultSize)

                dotRadius =
                    getDimension(
                        R.styleable.LightsLoader_lights_dotRadius,
                        defaultDotRadius
                    )
                spacing =
                    getDimensionPixelSize(
                        R.styleable.LightsLoader_lights_spacing,
                        defaultSpacing
                    )

                dotColor = getColor(
                    R.styleable.LightsLoader_lights_dotColor,
                    defaultDotColor
                )
            }
        } finally {
            typedArray.recycle()
        }
    }

    override fun initViews() {
        dotDiameter = (2 * dotRadius).toInt()

        this.orientation = VERTICAL

        if (calWidthHeight == 0) {
            calWidthHeight = (dotDiameter * size) + ((size - 1) * spacing)
        }

        for (countI in 0 until size) {
            val linearLayout = LinearLayout(context).apply {
                orientation = HORIZONTAL
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
                ).apply {
                    if (countI != 0) topMargin = spacing
                }
            }

            for (countJ in 0 until size) {
                val dotView = DotView(context, dotRadius, dotColor)

                val innerParam = LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
                ).apply {
                    if (countJ != 0) leftMargin = spacing
                }

                linearLayout.addView(dotView, innerParam)
                dotsList.add(dotView)
            }

            addView(linearLayout)
        }
    }


    // Animation controls
    override fun playAnimationLoop() {
        for (count in 0 until size) {
            for (item in dotsList) {
                item.startAnimation(getAlphaAnimation())
            }
        }
    }

    override fun clearPreviousAnimations() {
        for (count in 0 until size) {
            for (item in dotsList) {
                item.clearAnimation()
            }
        }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (calWidthHeight == 0) {
            calWidthHeight = (dotDiameter * size) + ((size - 1) * spacing)
        }

        setMeasuredDimension(calWidthHeight, calWidthHeight)
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

