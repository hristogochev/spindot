package com.hristogochev.spinkit.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import com.hristogochev.spinkit.R
import com.hristogochev.spinkit.animation.AnimationLayout
import com.hristogochev.spinkit.basicviews.DotView
import com.hristogochev.spinkit.utils.getColorResource
import com.hristogochev.spinkit.utils.onAnimationEnd

/**
 * Created by agrawalsuneet on 9/1/18.
 *
 * Modifier by hristogochev on 02/02/23.
 */

class FidgetLoader : AnimationLayout {
    // Default input attributes
    private val defaultDotRadius = 50f
    private val defaultDistanceMultiplier = 4
    private val defaultFirstDotColor = getColorResource(R.color.loader_selected)
    private val defaultSecondDotColor = getColorResource(R.color.loader_selected)
    private val defaultThirdDotColor = getColorResource(R.color.loader_selected)
    private val defaultDrawOnlyStroke = false
    private val defaultStrokeWidth = 20
    private val defaultAnimDuration: Long = 500

    // Settable attributes
    private var dotRadius = defaultDotRadius
    private var distanceMultiplier = defaultDistanceMultiplier
        set(value) {
            field = if (value < 1) 1 else value
        }
    private var firstDotColor = defaultFirstDotColor
    private var secondDotColor = defaultSecondDotColor
    private var thirdDotColor = defaultThirdDotColor
    private var drawOnlyStroke = defaultDrawOnlyStroke
    private var strokeWidth = defaultStrokeWidth
    private var animDuration = defaultAnimDuration

    // Views
    private lateinit var firstDot: DotView
    private lateinit var secondDot: DotView
    private lateinit var thirdDot: DotView
    private lateinit var relativeLayout: RelativeLayout

    // Animation attributes
    private var step: Int = 0
    private var calWidthHeight: Int = 0
    private var posArrayList: ArrayList<ArrayList<Pair<Float, Float>>> = ArrayList()
    private val accelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()

    // General
    private var dotsDiameter = (2 * dotRadius).toInt()

    // Custom constructors
    constructor(
        context: Context,
        dotRadius: Float? = null,
        distanceMultiplier: Int? = null,
        drawOnlyStroke: Boolean? = null,
        strokeWidth: Int? = null,
        firstDotColor: Int? = null,
        secondDotColor: Int? = null,
        thirdDotColor: Int? = null,
        animDuration: Long? = null,
        toggleOnVisibilityChange: Boolean? = null
    ) : super(context) {
        this.dotRadius = dotRadius ?: defaultDotRadius
        this.distanceMultiplier = distanceMultiplier ?: defaultDistanceMultiplier
        this.drawOnlyStroke = drawOnlyStroke ?: defaultDrawOnlyStroke
        this.strokeWidth = strokeWidth ?: defaultStrokeWidth
        this.firstDotColor = firstDotColor ?: defaultFirstDotColor
        this.secondDotColor = secondDotColor ?: defaultSecondDotColor
        this.thirdDotColor = thirdDotColor ?: defaultThirdDotColor
        this.animDuration = animDuration ?: defaultAnimDuration
        this.toggleOnVisibilityChange = toggleOnVisibilityChange ?: defaultToggleOnVisibilityChange
        initViews()
        initPositions()
    }

    // Default constructors
    constructor(context: Context) : super(context) {
        initViews()
        initPositions()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initViews()
        initPositions()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttributes(attrs)
        initViews()
        initPositions()
    }


    // Initialization functions
    override fun initAttributes(attrs: AttributeSet) {
        super.initAttributes(attrs)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FidgetLoader, 0, 0)

        try {
            with(typedArray) {
                dotRadius = getDimension(
                    R.styleable.FidgetLoader_fidget_dotRadius,
                    defaultDotRadius
                )
                distanceMultiplier = getInteger(
                    R.styleable.FidgetLoader_fidget_distanceMultiplier,
                    defaultDistanceMultiplier
                )
                firstDotColor = getColor(
                    R.styleable.FidgetLoader_fidget_firstDotColor,
                    defaultFirstDotColor
                )
                secondDotColor = getColor(
                    R.styleable.FidgetLoader_fidget_secondDotColor,
                    defaultSecondDotColor
                )
                thirdDotColor = getColor(
                    R.styleable.FidgetLoader_fidget_thirdDotColor,
                    defaultThirdDotColor
                )
                drawOnlyStroke = getBoolean(
                    R.styleable.FidgetLoader_fidget_drawOnlyStroke,
                    defaultDrawOnlyStroke
                )
                if (drawOnlyStroke) {
                    strokeWidth = getDimensionPixelSize(
                        R.styleable.FidgetLoader_fidget_strokeWidth,
                        defaultStrokeWidth
                    )
                }
                animDuration =
                    getInt(
                        R.styleable.FidgetLoader_fidget_animDuration,
                        defaultAnimDuration.toInt()
                    ).toLong()
            }
        } finally {
            typedArray.recycle()
        }
    }

    override fun initViews() {
        this.dotsDiameter = (2 * dotRadius).toInt()

        this.gravity = Gravity.CENTER_HORIZONTAL

        relativeLayout = RelativeLayout(context).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }

        if (calWidthHeight == 0) {
            calWidthHeight = (dotsDiameter * distanceMultiplier) + strokeWidth
        }

        firstDot = DotView(context, dotRadius, firstDotColor, drawOnlyStroke, strokeWidth)
        secondDot = DotView(context, dotRadius, secondDotColor, drawOnlyStroke, strokeWidth)
        thirdDot = DotView(context, dotRadius, thirdDotColor, drawOnlyStroke, strokeWidth)

        val firstParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
            addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        }
        val secondParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        }
        val thirdParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        }

        relativeLayout.addView(firstDot, firstParam)
        relativeLayout.addView(secondDot, secondParam)
        relativeLayout.addView(thirdDot, thirdParam)

        val relParam = RelativeLayout.LayoutParams(calWidthHeight, calWidthHeight)

        this.addView(relativeLayout, relParam)
    }

    private fun initPositions() {
        val fullDistance = (calWidthHeight - ((2 * dotRadius) + strokeWidth))
        val halfDistance = fullDistance / 2

        val firstPosArray = ArrayList<Pair<Float, Float>>().apply {
            add(Pair(0.0f, 0.0f))
            add(Pair(halfDistance, fullDistance))
            add(Pair(-halfDistance, fullDistance))
        }
        val secondPosArray = ArrayList<Pair<Float, Float>>().apply {
            add(Pair(0.0f, 0.0f))
            add(Pair(-fullDistance, 0.0f))
            add(Pair(-halfDistance, -fullDistance))
        }
        val thirdPosArray = ArrayList<Pair<Float, Float>>().apply {
            add(Pair(0.0f, 0.0f))
            add(Pair(halfDistance, -fullDistance))
            add(Pair(fullDistance, 0.0f))
        }

        posArrayList.add(firstPosArray)
        posArrayList.add(secondPosArray)
        posArrayList.add(thirdPosArray)
    }


    // Animation controls
    override fun playAnimationLoop() {
        if (animationStopped) return
        val firstCircleAnim = getTranslateAnim(1)
        val secondCircleAnim = getTranslateAnim(2)
        val thirdCircleAnim = getTranslateAnim(3).apply {
            onAnimationEnd {
                step++
                if (step > 2) step = 0
                playAnimationLoop()
            }
        }

        firstDot.startAnimation(firstCircleAnim)
        secondDot.startAnimation(secondCircleAnim)
        thirdDot.startAnimation(thirdCircleAnim)
    }

    override fun clearPreviousAnimations() {
        firstDot.clearAnimation()
        secondDot.clearAnimation()
        thirdDot.clearAnimation()
    }

    // Animations
    private fun getTranslateAnim(circleCount: Int): TranslateAnimation {

        var nextStep = step + 1
        if (nextStep > 2) nextStep = 0

        val fromXPos = posArrayList[circleCount - 1][step].first
        val fromYPos = posArrayList[circleCount - 1][step].second

        val toXPos = posArrayList[circleCount - 1][nextStep].first
        val toYPos = posArrayList[circleCount - 1][nextStep].second

        return TranslateAnimation(fromXPos, toXPos, fromYPos, toYPos).apply {
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
            calWidthHeight = (dotsDiameter * distanceMultiplier) + strokeWidth
        }
        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }
}