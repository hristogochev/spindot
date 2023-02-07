package com.hristogochev.dotloaders.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import com.hristogochev.dotloaders.R
import com.hristogochev.dotloaders.animation.AnimationLayout
import com.hristogochev.dotloaders.basicviews.DotView
import com.hristogochev.dotloaders.utils.getColorResource
import com.hristogochev.dotloaders.utils.onAnimationEnd

/**
 * Created by agrawalsuneet on 8/26/18.
 *
 * Modified by hristogochev on 02/02/23.
 */

class ZeeLoader : AnimationLayout {

    // Default input attributes
    private val defaultDotRadius = 50f
    private val defaultDistanceMultiplier = 4
    private val defaultFirstDotColor = getColorResource(R.color.loader_selected)
    private val defaultSecondDotColor = getColorResource(R.color.loader_selected)
    private val defaultAnimDuration: Long = 500

    // Settable attributes
    private var dotRadius = defaultDotRadius
    private var distanceMultiplier = defaultDistanceMultiplier
        set(value) {
            field = if (value < 1) 1 else value
        }
    private var firstDotColor = defaultFirstDotColor
    private var secondDotColor = defaultSecondDotColor
    private var animDuration = defaultAnimDuration

    // Views
    private var calWidthHeight: Int = 0
    private lateinit var firstDot: DotView
    private lateinit var secondDot: DotView
    private lateinit var relativeLayout: RelativeLayout

    // Animation attributes
    private var step: Int = 0
    private val accelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()

    // Custom constructors
    constructor(
        context: Context,
        dotRadius: Float? = null,
        distanceMultiplier: Int? = null,
        firstDotColor: Int? = null,
        secondDotColor: Int? = null,
        animDuration: Long? = null,
        toggleOnVisibilityChange: Boolean? = null

    ) : super(context) {
        this.dotRadius = dotRadius ?: defaultDotRadius
        this.distanceMultiplier = distanceMultiplier ?: defaultDistanceMultiplier
        this.firstDotColor = firstDotColor ?: defaultFirstDotColor
        this.secondDotColor = secondDotColor ?: defaultSecondDotColor
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

    // Initialization functions
    override fun initAttributes(attrs: AttributeSet) {
        super.initAttributes(attrs)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZeeLoader, 0, 0)

        try {
            with(typedArray) {
                dotRadius = getDimension(
                    R.styleable.ZeeLoader_zee_dotRadius,
                    defaultDotRadius
                )
                distanceMultiplier = getInteger(
                    R.styleable.ZeeLoader_zee_distanceMultiplier,
                    defaultDistanceMultiplier
                )
                firstDotColor = getColor(
                    R.styleable.ZeeLoader_zee_firstDotColor,
                    defaultFirstDotColor
                )
                secondDotColor = getColor(
                    R.styleable.ZeeLoader_zee_secondDotColor,
                    defaultSecondDotColor
                )
                animDuration =
                    getInt(
                        R.styleable.ZeeLoader_zee_animDuration,
                        defaultAnimDuration.toInt()
                    ).toLong()
            }
        } finally {
            typedArray.recycle()
        }
    }


    override fun initViews() {
        this.gravity = Gravity.CENTER_HORIZONTAL

        relativeLayout = RelativeLayout(context).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }

        if (calWidthHeight == 0) calWidthHeight = (2 * dotRadius * distanceMultiplier).toInt()

        firstDot = DotView(context, dotRadius, firstDotColor)
        val firstParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
            addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        }

        secondDot = DotView(context, dotRadius, secondDotColor)
        val secondParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        }

        relativeLayout.addView(firstDot, firstParam)
        relativeLayout.addView(secondDot, secondParam)

        val relParam = RelativeLayout.LayoutParams(calWidthHeight, calWidthHeight)

        addView(relativeLayout, relParam)
    }

    // Animation controls
    override fun playAnimationLoop() {
        val firstCircleAnim = getTranslateAnim(1)

        firstDot.startAnimation(firstCircleAnim)

        val secondCircleAnim = getTranslateAnim(2)

        secondCircleAnim.onAnimationEnd {
            step++
            if (step > 3) {
                step = 0
            }
            if (!animationStopped) playAnimationLoop()
        }

        secondDot.startAnimation(secondCircleAnim)
    }

    override fun clearPreviousAnimations() {
        firstDot.clearAnimation()
        secondDot.clearAnimation()
    }

    // Animations
    private fun getTranslateAnim(circleCount: Int): TranslateAnimation {
        val circleDiameter = 2 * dotRadius
        val finalDistance = ((distanceMultiplier - 1) * circleDiameter)

        var fromXPos = 0.0f
        var fromYPos = 0.0f

        var toXPos = 0.0f
        var toYPos = 0.0f

        when (step) {
            0 -> {
                toXPos = if (circleCount == 1) finalDistance else -1 * finalDistance
            }
            1 -> {
                if (circleCount == 1) {
                    fromXPos = finalDistance
                    toYPos = finalDistance
                } else {
                    fromXPos = -1 * finalDistance
                    toYPos = -1 * finalDistance
                }
            }
            2 -> {
                if (circleCount == 1) {
                    toXPos = finalDistance
                    fromYPos = finalDistance
                    toYPos = fromYPos
                } else {
                    toXPos = -1 * finalDistance
                    fromYPos = -1 * finalDistance
                    toYPos = fromYPos
                }
            }
            3 -> {
                if (circleCount == 1) {
                    fromXPos = finalDistance
                    fromYPos = finalDistance
                } else {
                    fromXPos = -1 * finalDistance
                    fromYPos = -1 * finalDistance
                }
            }
        }

        return TranslateAnimation(
            fromXPos, toXPos,
            fromYPos, toYPos
        ).apply {
            duration = animDuration
            fillAfter = true
            interpolator = accelerateDecelerateInterpolator
            repeatCount = 0
        }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (calWidthHeight == 0) {
            calWidthHeight = (2 * dotRadius * distanceMultiplier).toInt()
        }
        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }
}