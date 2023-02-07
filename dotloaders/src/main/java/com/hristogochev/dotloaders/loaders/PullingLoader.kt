package com.hristogochev.dotloaders.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.animation.*
import com.hristogochev.dotloaders.R
import com.hristogochev.dotloaders.animation.AnimationLayout
import com.hristogochev.dotloaders.basicviews.DotsView
import com.hristogochev.dotloaders.utils.getColorResource
import com.hristogochev.dotloaders.utils.onAnimationEnd

/**
 * Modified by hristogochev on 02/02/23.
 */

class PullingLoader : AnimationLayout {

    // Default input attributes
    private val defaultDotRadius = 30f
    private val defaultUseMultipleColors = false
    private val defaultDotColor = getColorResource(android.R.color.darker_gray)
    private var defaultDotColors = IntArray(8) { defaultDotColor }
    private val defaultRadius = 90f
    private val defaultAnimDuration: Long = 2000

    // Input attributes
    private var dotRadius = defaultDotRadius
    private var useMultipleColors = defaultUseMultipleColors
    private var dotColor = defaultDotColor
    private var dotColors = defaultDotColors
    private var radius = defaultRadius
    private var animDuration = defaultAnimDuration

    // Base view
    private lateinit var dotsView: DotsView

    // Animation attributes
    private val accelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()
    private val accelerateInterpolator = AccelerateInterpolator()

    // Single color constructor
    constructor(
        context: Context,
        dotsRadius: Float? = null,
        radius: Float? = null,
        dotColor: Int? = null,
        animDuration: Long? = null,
        toggleOnVisibilityChange: Boolean? = null
    ) : super(
        context
    ) {
        this.dotRadius = dotsRadius ?: defaultDotRadius
        this.radius = radius ?: defaultRadius
        this.dotColor = dotColor ?: defaultDotColor
        this.useMultipleColors = false
        this.animDuration = animDuration ?: defaultAnimDuration
        this.toggleOnVisibilityChange = toggleOnVisibilityChange ?: defaultToggleOnVisibilityChange
        initViews()
    }

    // Multiple colors constructor
    constructor(
        context: Context,
        dotRadius: Float? = null,
        radius: Float? = null,
        animDuration: Long? = null,
        dotColors: IntArray? = null,
        toggleOnVisibilityChange: Boolean? = null
    ) : super(context) {
        this.dotRadius = dotRadius ?: defaultDotRadius
        this.radius = radius ?: defaultRadius
        this.dotColors = dotColors ?: defaultDotColors
        this.useMultipleColors = true
        this.animDuration = animDuration ?: defaultAnimDuration
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

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullingLoader, 0, 0)

        try {
            with(typedArray) {
                dotRadius =
                    getDimension(
                        R.styleable.PullingLoader_pulling_dotRadius,
                        defaultDotRadius
                    )
                useMultipleColors =
                    getBoolean(
                        R.styleable.PullingLoader_pulling_useMultipleColors,
                        defaultUseMultipleColors
                    )
                if (useMultipleColors) {
                    val dotsArrayId =
                        getResourceId(
                            R.styleable.PullingLoader_pulling_dotColors,
                            0
                        )
                    dotColors =
                        if (dotsArrayId != 0) calcDotColorsArray(dotsArrayId, defaultDotColor)
                        else defaultDotColors
                } else {
                    dotColor = getColor(
                        R.styleable.PullingLoader_pulling_dotColor,
                        defaultDotColor
                    )
                }
                radius =
                    getDimension(
                        R.styleable.PullingLoader_pulling_radius,
                        defaultRadius
                    )
                animDuration =
                    getInt(
                        R.styleable.PullingLoader_pulling_animDuration,
                        defaultAnimDuration.toInt()
                    ).toLong()
            }
        } finally {
            typedArray.recycle()
        }
    }

    override fun initViews() {
        dotsView = if (useMultipleColors) {
            DotsView(context, dotRadius, radius, dotColors)
        } else {
            DotsView(context, dotRadius, radius, dotColor)
        }

        addView(dotsView)
    }


    // Animation controls
    override fun playAnimationLoop() {
        val rotationAnim = getRotateAnimation()
        rotationAnim.onAnimationEnd {
            val scaleAnimation = getScaleAnimation()
            scaleAnimation.onAnimationEnd {
                if (!animationStopped) playAnimationLoop()
            }
            dotsView.startAnimation(scaleAnimation)
        }

        dotsView.startAnimation(rotationAnim)
    }

    override fun clearPreviousAnimations() {
        dotsView.clearAnimation()
    }

    // Animations
    private fun getRotateAnimation(): RotateAnimation {
        return RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = animDuration
            fillAfter = true
            repeatCount = 0
            interpolator = accelerateDecelerateInterpolator
        }
    }

    private fun getScaleAnimation(): AnimationSet {
        val scaleAnimation = ScaleAnimation(
            1.0f, 0.5f,
            1.0f, 0.5f,
            (dotsView.width / 2).toFloat(),
            (dotsView.height / 2).toFloat()
        ).apply {
            repeatCount = 1
            repeatMode = Animation.REVERSE
        }

        val alphaAnimation = AlphaAnimation(1.0f, 0.0f).apply {
            repeatCount = 1
            repeatMode = Animation.REVERSE
        }

        val animSet = AnimationSet(true).apply {
            addAnimation(scaleAnimation)
            addAnimation(alphaAnimation)
            repeatCount = 1
            repeatMode = Animation.REVERSE
            duration = if (animDuration > 0) (animDuration / 8) else 100
            interpolator = accelerateInterpolator
        }

        return animSet
    }


    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidthHeight = (2 * this.radius + 2 * dotRadius).toInt()
        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }


    // Utility functions
    private fun calcDotColorsArray(arrayId: Int, defaultColor: Int): IntArray {
        val colors = IntArray(8)
        val colorsArray = resources.getIntArray(arrayId)
        for (i in 0..7) {
            colors[i] = if (colorsArray.size > i) colorsArray[i] else defaultColor
        }
        return colors
    }
}