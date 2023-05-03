/**
 * Created by suneet on 12/13/17.
 *
 * Modified by hristogochev on 02/02/23.
 */

package com.hristogochev.spindot.loaders

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.animation.*
import com.hristogochev.spindot.R
import com.hristogochev.spindot.animation.AnimationLayout
import com.hristogochev.spindot.basicviews.DotView
import com.hristogochev.spindot.utils.getColorResource
import com.hristogochev.spindot.utils.onAnimationEnd

class SlidingLoader : com.hristogochev.spindot.animation.AnimationLayout {

    // Default input attributes
    private val defaultDotRadius = 30f
    private val defaultSpacing = 15
    private val defaultFirstDotColor = getColorResource(R.color.loader_selected)
    private val defaultSecondDotColor = getColorResource(R.color.loader_selected)
    private val defaultThirdDotColor = getColorResource(R.color.loader_selected)
    private val defaultAnimDuration: Long = 500
    private val defaultDistanceToMove = 12

    // Settable attributes
    private var dotRadius = defaultDotRadius
    private var spacing = defaultSpacing
    private var firstDotColor = defaultFirstDotColor
    private var secondDotColor = defaultSecondDotColor
    private var thirdDotColor = defaultThirdDotColor
    private var animDuration = defaultAnimDuration
        set(value) {
            field = value
            firstDelayDuration = value / 10
            secondDelayDuration = value / 5
        }
    private var distanceToMove = defaultDistanceToMove
        set(value) {
            field = value
            invalidate()
        }

    // Animation attributes
    private val anticipateOvershootInterpolator = AnticipateOvershootInterpolator()
    private var firstDelayDuration = defaultAnimDuration / 10
    private var secondDelayDuration = defaultAnimDuration / 5
    private var isForwardDir = true

    // Views
    private lateinit var firstDot: com.hristogochev.spindot.basicviews.DotView
    private lateinit var secondDot: com.hristogochev.spindot.basicviews.DotView
    private lateinit var thirdDot: com.hristogochev.spindot.basicviews.DotView


    // Custom constructors
    constructor(
        context: Context,
        dotRadius: Float? = null,
        spacing: Int? = null,
        firstDotColor: Int? = null,
        secondDotColor: Int? = null,
        thirdDotColor: Int? = null,
        animDuration: Long? = null,
        distanceToMove: Int? = null,
        toggleOnVisibilityChange: Boolean? = null
    ) : super(context) {
        this.dotRadius = dotRadius ?: defaultDotRadius
        this.spacing = spacing ?: defaultSpacing
        this.firstDotColor = firstDotColor ?: defaultFirstDotColor
        this.secondDotColor = secondDotColor ?: defaultSecondDotColor
        this.thirdDotColor = thirdDotColor ?: defaultThirdDotColor
        this.animDuration = animDuration ?: defaultAnimDuration
        this.distanceToMove = distanceToMove ?: defaultDistanceToMove
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

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidingLoader, 0, 0)

        try {
            with(typedArray) {
                dotRadius =
                    getDimension(
                        R.styleable.SlidingLoader_sliding_dotRadius,
                        defaultDotRadius
                    )
                spacing =
                    getDimensionPixelSize(
                        R.styleable.SlidingLoader_sliding_spacing,
                        defaultSpacing
                    )
                firstDotColor = getColor(
                    R.styleable.SlidingLoader_sliding_firstDotColor,
                    defaultFirstDotColor
                )
                secondDotColor = getColor(
                    R.styleable.SlidingLoader_sliding_secondDotColor,
                    defaultSecondDotColor
                )
                thirdDotColor = getColor(
                    R.styleable.SlidingLoader_sliding_thirdDotColor,
                    defaultThirdDotColor
                )
                animDuration =
                    getInt(
                        R.styleable.SlidingLoader_sliding_animDuration,
                        defaultAnimDuration.toInt()
                    ).toLong()
                distanceToMove =
                    getInteger(
                        R.styleable.SlidingLoader_sliding_distanceToMove,
                        defaultDistanceToMove
                    )
            }
        } finally {
            typedArray.recycle()
        }
    }

    override fun initViews() {
        firstDot = com.hristogochev.spindot.basicviews.DotView(context, dotRadius, firstDotColor)
        secondDot = com.hristogochev.spindot.basicviews.DotView(context, dotRadius, secondDotColor)
        thirdDot = com.hristogochev.spindot.basicviews.DotView(context, dotRadius, thirdDotColor)

        val size = (2 * dotRadius).toInt()

        val paramsFirstCircle = LayoutParams(size, size)
        paramsFirstCircle.leftMargin = size

        val paramsSecondCircle = LayoutParams(size, size)
        paramsSecondCircle.leftMargin = spacing

        val paramsThirdCircle = LayoutParams(size, size)
        paramsThirdCircle.leftMargin = spacing
        paramsThirdCircle.rightMargin = size

        addView(firstDot, paramsFirstCircle)
        addView(secondDot, paramsSecondCircle)
        addView(thirdDot, paramsThirdCircle)
    }


    // Animation controls
    override fun playAnimationLoop() {
        if (animationStopped) return

        val trans1Anim = getTranslateAnim(isForwardDir)
        if (isForwardDir) thirdDot.startAnimation(trans1Anim) else firstDot.startAnimation(
            trans1Anim
        )

        val trans2Anim = getTranslateAnim(isForwardDir)

        Handler(Looper.getMainLooper()).postDelayed({
            secondDot.startAnimation(trans2Anim)
        }, firstDelayDuration)

        val trans3Anim = getTranslateAnim(isForwardDir)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isForwardDir) firstDot.startAnimation(trans3Anim) else thirdDot.startAnimation(
                trans3Anim
            )
        }, secondDelayDuration)

        trans3Anim.onAnimationEnd {
            isForwardDir = !isForwardDir
            playAnimationLoop()
        }
    }

    override fun clearPreviousAnimations() {
        firstDot.clearAnimation()
        secondDot.clearAnimation()
        thirdDot.clearAnimation()
    }


    // Animations
    private fun getTranslateAnim(isForwardDir: Boolean): TranslateAnimation {
        return TranslateAnimation(
            if (isForwardDir) 0f else (distanceToMove * dotRadius),
            if (isForwardDir) (distanceToMove * dotRadius) else 0f,
            0f, 0f
        ).apply {
            duration = animDuration
            fillAfter = true
            interpolator = anticipateOvershootInterpolator
        }
    }

    // Overrides
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidth = ((10 * dotRadius) + (distanceToMove * dotRadius) + (2 * spacing)).toInt()
        val calHeight = (2 * dotRadius).toInt()

        setMeasuredDimension(calWidth, calHeight)
    }
}